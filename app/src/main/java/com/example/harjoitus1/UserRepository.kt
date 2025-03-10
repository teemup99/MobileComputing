package com.example.harjoitus1

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class UserRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()

    suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            Log.d("UserRepository", "Saving user: $user")
            userDao.insertUser(user)
        }
    }

    suspend fun getUser(): User? {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUser()
            Log.d("UserRepository", "Retrieved user: $user")
            userDao.getUser()
        }
    }
}
