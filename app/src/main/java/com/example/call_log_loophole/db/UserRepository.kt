package com.example.call_log_loophole.db

import androidx.lifecycle.LiveData
import com.example.call_log_loophole.models.User

class UserRepository(private val userDao: UserDao) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) = userDao.insertOrUpdate(user)

    suspend fun delete(user: User) = userDao.delete(user)
}