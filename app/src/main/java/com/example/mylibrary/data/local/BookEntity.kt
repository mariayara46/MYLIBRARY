
package com.example.mylibrary.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,
    val title: String,
    val author: String,
    val readDate: String? = null,
    val notes: String? = null
)
