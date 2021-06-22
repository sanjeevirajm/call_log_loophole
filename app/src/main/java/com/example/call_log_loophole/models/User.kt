package com.example.call_log_loophole.models

import androidx.room.*

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey @ColumnInfo(name = "phone_number") val phoneNumber: String
)