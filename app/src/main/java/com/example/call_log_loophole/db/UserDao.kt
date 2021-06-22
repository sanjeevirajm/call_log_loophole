package com.example.call_log_loophole.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.call_log_loophole.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    @Delete
    suspend fun delete(user: User)
}