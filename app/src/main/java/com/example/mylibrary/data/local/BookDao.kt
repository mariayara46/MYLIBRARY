
package com.example.mylibrary.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // CREAR un libro
    @Insert
    suspend fun insert(book: BookEntity): Long

    // CONSULTAR todos los libros del usuario
    @Query("""
        SELECT * FROM books
        WHERE userId = :userId
        ORDER BY id DESC
    """)
    fun getBooks(userId: Long): Flow<List<BookEntity>>

    // CONSULTAR el detalle de un libro
    @Query("""
        SELECT * FROM books
        WHERE id = :bookId AND userId = :userId
        LIMIT 1
    """)
    suspend fun getBook(
        bookId: Long,
        userId: Long
    ): BookEntity?

    // ACTUALIZAR un libro
    @Query("""
        UPDATE books
        SET title = :title,
            author = :author,
            readDate = :readDate,
            notes = :notes
        WHERE id = :bookId AND userId = :userId
    """)
    suspend fun updateBook(
        bookId: Long,
        userId: Long,
        title: String,
        author: String,
        readDate: String?,
        notes: String?
    ): Int

    // ELIMINAR un libro
    @Query("""
        DELETE FROM books
        WHERE id = :bookId AND userId = :userId
    """)
    suspend fun deleteBook(
        bookId: Long,
        userId: Long
    ): Int
}
