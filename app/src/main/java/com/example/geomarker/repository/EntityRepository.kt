package com.example.geomarker.repository

import android.util.Log
import com.example.geomarker.api.RetrofitClient
import com.example.geomarker.model.Entity
import com.example.geomarker.model.EntityDao
import kotlinx.coroutines.flow.Flow

class EntityRepository(private val entityDao: EntityDao) {

    val allEntities: Flow<List<Entity>> = entityDao.getAllEntities()

    suspend fun insert(entity: Entity) {
        entityDao.insert(entity)
    }

    suspend fun update(entity: Entity) {
        entityDao.update(entity)
    }

    suspend fun deleteById(entityId: Int) {
        entityDao.deleteById(entityId)
    }

    suspend fun getEntityById(entityId: Int): Entity? {
        return entityDao.getEntityById(entityId)
    }

    suspend fun deleteAllEntities() {
        entityDao.deleteAllEntities()
    }

    suspend fun refreshEntities() {
        try {
            val response = RetrofitClient.apiService.getEntities()
            if (response.isSuccessful) {
                val entities = response.body()
                entities?.let {
                    Log.d("EntityRepository", "Total entities fetched from API: ${it.size}")
                    val validEntities = it.filter { entity ->
                        val latValid = entity.lat?.toDoubleOrNull() != null
                        val lonValid = entity.lon?.toDoubleOrNull() != null
                        val imageUrlValid = entity.imageUrl != null && entity.imageUrl.isNotBlank()
                        val isValid = latValid && lonValid && imageUrlValid
                        if (!isValid) {
                            Log.d("EntityRepository", "Filtering out entity: ${entity.title} (Lat: ${entity.lat}, Lon: ${entity.lon}, Image: ${entity.imageUrl})")
                        }
                        isValid
                    }
                    entityDao.deleteAllEntities()
                    validEntities.forEach { entity ->
                        entityDao.insert(entity)
                    }
                    Log.d("EntityRepository", "Found ${validEntities.size} valid entities after filtering and saved to local DB.")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("EntityRepository", "API Error fetching entities: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Log.e("EntityRepository", "Error fetching entities from API: ${e.message}", e)
        }
    }
}