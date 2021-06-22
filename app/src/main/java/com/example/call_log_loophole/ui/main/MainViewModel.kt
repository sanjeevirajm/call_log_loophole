package com.example.call_log_loophole.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.call_log_loophole.db.UserDatabase
import com.example.call_log_loophole.db.UserRepository
import com.example.call_log_loophole.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: UserRepository
    val users: LiveData<List<User>>

    init {
        val userDao = UserDatabase.getDatabase(app).userDao()
        repository = UserRepository(userDao)
        users = repository.allUsers
    }

    fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(user)
    }
}
