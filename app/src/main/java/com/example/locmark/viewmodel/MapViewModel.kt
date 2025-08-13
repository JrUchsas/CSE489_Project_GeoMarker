package com.example.locmark.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.locmark.AppDatabase
import com.example.locmark.model.Entity
import com.example.locmark.repository.EntityRepository
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntityRepository
    val entities: LiveData<List<Entity>>

    init {
        val entityDao = AppDatabase.getDatabase(application).entityDao()
        repository = EntityRepository(entityDao)
        entities = repository.allEntities.asLiveData(viewModelScope.coroutineContext)
    }

    fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            repository.deleteById(entityId)
        }
    }
}

