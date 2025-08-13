package com.example.locmark.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.locmark.AppDatabase
import com.example.locmark.model.Entity
import com.example.locmark.repository.EntityRepository
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EntityFormViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntityRepository
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> get() = _saveResult
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        val entityDao = AppDatabase.getDatabase(application).entityDao()
        repository = EntityRepository(entityDao)
    }

    fun createEntity(title: String, lat: Double, lon: Double, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val imagePath = imageUri?.toString() // Store URI as string

                if (imagePath == null) {
                    _error.postValue("Image URI is null")
                    _saveResult.postValue(false)
                    return@launch
                }

                val entity = Entity(title = title, lat = lat, lon = lon, imagePath = imagePath)
                Log.d("EntityFormViewModel", "Attempting to save entity to local DB: $entity")
                repository.insert(entity)
                Log.d("EntityFormViewModel", "Entity saved to local DB successfully.")
                _saveResult.postValue(true)
            } catch (e: Exception) {
                Log.e("EntityFormViewModel", "Error saving entity to local DB: ${e.message}", e)
                _error.postValue(e.message)
            }
        }
    }

    fun updateEntity(id: Int, title: String, lat: Double, lon: Double, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imagePath = imageUri?.toString()
                if (imagePath == null) {
                    _error.postValue("Image URI is null")
                    _saveResult.postValue(false)
                    return@launch
                }
                val entity = Entity(id = id, title = title, lat = lat, lon = lon, imagePath = imagePath)
                repository.update(entity)
                _saveResult.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message)
                _saveResult.postValue(false)
            }
        }
    }

    fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(entityId)
                _saveResult.postValue(true) // Indicate success of deletion
            } catch (e: Exception) {
                _error.postValue(e.message)
                _saveResult.postValue(false)
            }
        }
    }

    suspend fun getEntityById(entityId: Int): Entity? {
        return repository.getEntityById(entityId)
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}

