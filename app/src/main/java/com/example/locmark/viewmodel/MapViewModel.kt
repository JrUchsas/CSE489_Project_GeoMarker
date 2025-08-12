package com.example.locmark.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locmark.model.Entity
import com.example.locmark.repository.EntityRepository
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val repository = EntityRepository()
    val entities: LiveData<List<Entity>> = repository.entities

    fun fetchEntities() {
        viewModelScope.launch {
            repository.fetchEntities()
        }
    }
}

