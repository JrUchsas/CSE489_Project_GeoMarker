package com.example.geomarker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.geomarker.AppDatabase
import com.example.geomarker.model.Entity
import com.example.geomarker.repository.EntityRepository
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntityRepository
    val entities: LiveData<List<Entity>>

    init {
        val entityDao = AppDatabase.getDatabase(application).entityDao()
        repository = EntityRepository(entityDao)
        entities = repository.allEntities.asLiveData(viewModelScope.coroutineContext)

        // Fetch entities from API and refresh local DB when ViewModel is initialized
        viewModelScope.launch {
            repository.refreshEntities()
        }
    }

    fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            repository.deleteById(entityId)
        }
    }
}