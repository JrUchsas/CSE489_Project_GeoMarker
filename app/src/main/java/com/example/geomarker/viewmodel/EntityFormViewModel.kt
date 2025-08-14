package com.example.geomarker.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.geomarker.AppDatabase
import com.example.geomarker.api.RetrofitClient
import com.example.geomarker.model.Entity
import com.example.geomarker.repository.EntityRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EntityFormViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntityRepository
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> get() = _saveResult
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        val entityDao = AppDatabase.getDatabase(application).entityDao()
        repository = EntityRepository(entityDao, AppDatabase.getDatabase(application))
    }

    fun createEntity(title: String, lat: Double, lon: Double, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val imageUrl = imageUri?.toString() // Store URI as string

                if (imageUrl == null) {
                    _error.postValue("Image URI is null")
                    _saveResult.postValue(false)
                    return@launch
                }

                // Save to local DB first (offline caching)
                val localEntity = Entity(title = title, lat = lat.toString(), lon = lon.toString(), imageUrl = imageUrl)
                Log.d("EntityFormViewModel", "Attempting to save entity to local DB: $localEntity")
                repository.insert(localEntity)
                Log.d("EntityFormViewModel", "Entity saved to local DB successfully.")

                // Prepare for API call
                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val latPart = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val lonPart = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imageFile = uriToFile(context, imageUri)
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )

                // Make API call
                val response = RetrofitClient.apiService.createEntity(
                    title = titlePart,
                    lat = latPart,
                    lon = lonPart,
                    image = imagePart
                )

                if (response.isSuccessful) {
                    Log.d("EntityFormViewModel", "Entity created on API successfully.")
                    _saveResult.postValue(true)
                    repository.refreshEntities() // Refresh local cache after successful API call
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EntityFormViewModel", "API Error: ${response.code()} - $errorBody")
                    _error.postValue("Failed to create entity on server: ${response.code()} - $errorBody")
                    _saveResult.postValue(false)
                }

            } catch (e: Exception) {
                Log.e("EntityFormViewModel", "Error creating entity: ${e.message}", e)
                _error.postValue(e.message)
                _saveResult.postValue(false)
            }
        }
    }

    fun updateEntity(id: Int, title: String, lat: Double, lon: Double, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imageUrl = imageUri?.toString()
                if (imageUrl == null) {
                    _error.postValue("Image URI is null")
                    _saveResult.postValue(false)
                    return@launch
                }

                // Update local DB first (offline caching)
                val localEntity = Entity(id = id, title = title, lat = lat.toString(), lon = lon.toString(), imageUrl = imageUrl)
                repository.update(localEntity)
                Log.d("EntityFormViewModel", "Entity updated in local DB successfully.")

                // Make API call
                val response = RetrofitClient.apiService.updateEntity(
                    id = id,
                    title = title,
                    lat = lat,
                    lon = lon,
                    image = imageUrl // Assuming image is sent as a URL string for update
                )

                if (response.isSuccessful) {
                    Log.d("EntityFormViewModel", "Entity updated on API successfully.")
                    _saveResult.postValue(true)
                    repository.refreshEntities() // Refresh local cache after successful API call
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EntityFormViewModel", "API Error: ${response.code()} - $errorBody")
                    _error.postValue("Failed to update entity on server: ${response.code()} - $errorBody")
                    _saveResult.postValue(false)
                }

            } catch (e: Exception) {
                Log.e("EntityFormViewModel", "Error updating entity: ${e.message}", e)
                _error.postValue(e.message)
                _saveResult.postValue(false)
            }
        }
    }

    fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            try {

                repository.deleteById(entityId)
                Log.d("EntityFormViewModel", "Entity deleted from local DB successfully.")


                val response = RetrofitClient.apiService.deleteEntity(entityId)

                if (response.isSuccessful) {
                    Log.d("EntityFormViewModel", "Entity deleted from API successfully.")
                    _saveResult.postValue(true) // Indicate success of deletion
                    repository.refreshEntities() // Refresh local cache after successful API call
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EntityFormViewModel", "API Error: ${response.code()} - $errorBody")
                    _error.postValue("Failed to delete entity from server: ${response.code()} - $errorBody")
                    _saveResult.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("EntityFormViewModel", "Error deleting entity: ${e.message}", e)
                _error.postValue(e.message)
                _saveResult.postValue(false)
            }
        }
    }

    suspend fun getEntityById(entityId: Int): Entity? {
        return repository.getEntityById(entityId)
    }

    private fun uriToFile(context: Application, uri: Uri): File {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}