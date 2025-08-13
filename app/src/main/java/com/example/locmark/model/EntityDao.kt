package com.example.locmark.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntityDao {
    @Query("SELECT * FROM entities ORDER BY id DESC")
    fun getAllEntities(): Flow<List<Entity>>

    @Insert
    suspend fun insert(entity: Entity)

    @Update
    suspend fun update(entity: Entity)

    @Query("DELETE FROM entities WHERE id = :entityId")
    suspend fun deleteById(entityId: Int)

    @Query("SELECT * FROM entities WHERE id = :entityId")
    suspend fun getEntityById(entityId: Int): Entity?
}
