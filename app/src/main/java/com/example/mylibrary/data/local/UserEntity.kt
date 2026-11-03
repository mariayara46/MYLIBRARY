
package com.example.mylibrary.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long = System.currentTimeMillis()
)
