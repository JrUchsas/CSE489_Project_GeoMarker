package com.example.locmark.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.locmark.model.Entity
import com.example.locmark.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EntityFormViewModel(application: Application) : AndroidViewModel(application) {
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> get() = _saveResult
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun createEntity(title: String, lat: Double, lon: Double, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val titleBody = RequestBody.create(MediaType.parse("text/plain"), title)
                val latBody = RequestBody.create(MediaType.parse("text/plain"), lat.toString())
                val lonBody = RequestBody.create(MediaType.parse("text/plain"), lon.toString())
                val imagePart = imageUri?.let {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val resized = resizeBitmap(bitmap, 800, 600)
                    val bos = ByteArrayOutputStream()
                    resized.compress(Bitmap.CompressFormat.JPEG, 90, bos)
                    val req = RequestBody.create(MediaType.parse("image/jpeg"), bos.toByteArray())
                    MultipartBody.Part.createFormData("image", "image.jpg", req)
                }
                val response = RetrofitClient.apiService.createEntity(titleBody, latBody, lonBody, imagePart!!)
                _saveResult.postValue(response.isSuccessful)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
