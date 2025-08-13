package com.example.locmark.repository

import com.example.locmark.model.Entity
import com.example.locmark.model.EntityDao
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
}

