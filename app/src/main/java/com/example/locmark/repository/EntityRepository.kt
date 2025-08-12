package com.example.locmark.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.locmark.model.Entity
import com.example.locmark.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntityRepository {
    private val _entities = MutableLiveData<List<Entity>>()
    val entities: LiveData<List<Entity>> get() = _entities

    suspend fun fetchEntities() {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getEntities()
                if (response.isSuccessful) {
                    _entities.postValue(response.body() ?: emptyList())
                } else {
                    _entities.postValue(emptyList())
                }
            } catch (e: Exception) {
                _entities.postValue(emptyList())
            }
        }
    }
}

