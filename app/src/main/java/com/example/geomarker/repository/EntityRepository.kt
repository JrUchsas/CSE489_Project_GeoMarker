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
                    entityDao.deleteAllEntities() // Clear existing data
                    it.forEach { entity ->
                        entityDao.insert(entity) // Insert new data from API
                    }
                    Log.d("EntityRepository", "Fetched ${it.size} entities from API and saved to local DB.")
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