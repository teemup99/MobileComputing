package com.example.harjoitus1

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.util.Log
import androidx.core.content.FileProvider

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application.applicationContext)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _userStateFlow = MutableStateFlow<User?>(null)
    val userStateFlow: StateFlow<User?> = _userStateFlow



    init {
        Log.d("MainViewModel", "ViewModel initialized")
        viewModelScope.launch {
            _userStateFlow.value = userRepository.getUser()
            _user.value = _userStateFlow.value
            Log.d("MainViewModel", "User data loaded: ${_userStateFlow.value}")
        }
    }

    fun saveUser(firstName: String, profilePictureUri: Uri?) {
        viewModelScope.launch {
            val profilePicUri = profilePictureUri?.let { saveImage(getApplication(), it) }
            val user = User(
                uid = 1,
                userName = firstName,
                profilePicUri = profilePicUri
            )

            userRepository.saveUser(user)
            _user.value = user
            _userStateFlow.value = user
        }
    }

    private fun saveImage(context: Context, imageUri: Uri): String? {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e("ImageSave", "Failed to open input stream for URI: $imageUri")
                return null
            }
            val file = File(context.filesDir, "profile_pictures")
            if (!file.exists()) file.mkdirs()

            val fileName = "${System.currentTimeMillis()}.jpg"
            val newFile = File(file, fileName)
            val outputStream = FileOutputStream(newFile)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                newFile
            )

            Log.d("ImageSave", "Saved image path: ${newFile.absolutePath}")
            return contentUri.toString()
        } catch (e: Exception) {
            Log.e("ImageSave", "Error saving image", e)
            return null
        }
    }
}
