package com.example.harjoitus1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User?
}
