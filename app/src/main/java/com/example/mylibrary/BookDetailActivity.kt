package com.example.mylibrary

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mylibrary.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Recuperar el usuario que inició sesión
        val preferences = getSharedPreferences(
            "mylibrary_session",
            MODE_PRIVATE
        )

        val userId = preferences.getLong("user_id", -1L)

        // Recuperar el identificador del libro seleccionado
        val bookId = intent.getLongExtra("book_id", -1L)

        if (userId <= 0L || bookId <= 0L) {
            Toast.makeText(
                this,
                "No se pudo identificar el libro",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        // Botón para regresar a Mis libros
        val btnBackToBooks = findViewById<Button>(R.id.btnBackToBooks)

        btnBackToBooks.setOnClickListener {
            finish()
        }

        // Botón para editar el libro seleccionado
        val btnEditBook = findViewById<Button>(R.id.btnEditBook)

        btnEditBook.setOnClickListener {
            val editIntent = Intent(
                this@BookDetailActivity,
                BookFormActivity::class.java
            )

            editIntent.putExtra("book_id", bookId)
            startActivity(editIntent)

            // Volver a la lista después de editar o cancelar
            finish()
        }

        // Conectarse con la base de datos Room
        val bookDao = AppDatabase
            .getDatabase(applicationContext)
            .bookDao()

        // Botón para eliminar el libro seleccionado
        val btnDeleteBook = findViewById<Button>(R.id.btnDeleteBook)

        btnDeleteBook.setOnClickListener {

            AlertDialog.Builder(this@BookDetailActivity)
                .setTitle("Eliminar libro")
                .setMessage(
                    "¿Estás seguro de que deseas eliminar este libro? " +
                            "Esta acción no se puede deshacer."
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar") { _, _ ->

                    lifecycleScope.launch {

                        val deletedRows = withContext(Dispatchers.IO) {
                            bookDao.deleteBook(bookId, userId)
                        }

                        if (deletedRows > 0) {
                            Toast.makeText(
                                this@BookDetailActivity,
                                "Libro eliminado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        } else {
                            Toast.makeText(
                                this@BookDetailActivity,
                                "No se pudo eliminar el libro",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .show()
        }

        // Consultar el libro sin bloquear la pantalla
        lifecycleScope.launch {

            val book = withContext(Dispatchers.IO) {
                bookDao.getBook(bookId, userId)
            }

            if (book == null) {
                Toast.makeText(
                    this@BookDetailActivity,
                    "Libro no encontrado",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            // Mostrar los datos en la pantalla
            findViewById<TextView>(
                R.id.tvDetailTitle
            ).text = book.title

            findViewById<TextView>(
                R.id.tvDetailAuthor
            ).text = book.author

            findViewById<TextView>(
                R.id.tvDetailReadDate
            ).text = book.readDate?.takeIf {
                it.isNotBlank()
            } ?: "Sin fecha"

            findViewById<TextView>(
                R.id.tvDetailNotes
            ).text = book.notes?.takeIf {
                it.isNotBlank()
            } ?: "Sin notas"
        }
    }
}
