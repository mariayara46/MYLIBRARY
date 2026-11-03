package com.example.mylibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mylibrary.data.local.AppDatabase
import com.example.mylibrary.data.local.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException

class BookFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_form)

        // 1. Obtener los campos del formulario
        val etTitle = findViewById<EditText>(R.id.etBookTitle)
        val etAuthor = findViewById<EditText>(R.id.etBookAuthor)
        val etDate = findViewById<EditText>(R.id.etReadDate)
        val etNotes = findViewById<EditText>(R.id.etBookNotes)
        val tvFormTitle = findViewById<TextView>(R.id.tvFormTitle)

        // 2. Obtener los botones
        val btnSave = findViewById<Button>(R.id.btnSaveBook)
        val btnCancel = findViewById<Button>(R.id.btnCancelBook)

        // 3. Recuperar la sesion del usuario
        val session = getSharedPreferences(
            "mylibrary_session",
            MODE_PRIVATE
        )

        val userId = session.getLong("user_id", -1L)

        if (userId == -1L) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
            return
        }

        // 4. Conectar con Room
        val database = AppDatabase.getDatabase(applicationContext)
        val bookDao = database.bookDao()

        // -1 indica que estamos creando un libro nuevo
        val bookId = intent.getLongExtra("book_id", -1L)

        // 5. Cargar los datos si vamos a editar un libro
        if (bookId != -1L) {

            tvFormTitle.text = "Editar libro"
            btnSave.text = "Guardar cambios"
            btnSave.isEnabled = false

            lifecycleScope.launch {
                try {
                    val book = withContext(Dispatchers.IO) {
                        bookDao.getBook(bookId, userId)
                    }

                    if (book == null) {
                        Toast.makeText(
                            this@BookFormActivity,
                            "Libro no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                        return@launch
                    }

                    etTitle.setText(book.title)
                    etAuthor.setText(book.author)
                    etDate.setText(book.readDate ?: "")
                    etNotes.setText(book.notes ?: "")

                    btnSave.isEnabled = true

                } catch (e: Exception) {
                    Toast.makeText(
                        this@BookFormActivity,
                        "Error al cargar el libro",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }

        // 6. Guardar o actualizar un libro
        btnSave.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val author = etAuthor.text.toString().trim()
            val date = etDate.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            // Validar titulo obligatorio
            if (title.isEmpty()) {
                etTitle.error = "El titulo es obligatorio"
                etTitle.requestFocus()
                return@setOnClickListener
            }

            // Validar autor obligatorio
            if (author.isEmpty()) {
                etAuthor.error = "El autor es obligatorio"
                etAuthor.requestFocus()
                return@setOnClickListener
            }

            // Validar fecha cuando se proporciona
            if (date.isNotEmpty()) {
                try {
                    LocalDate.parse(date)
                } catch (e: DateTimeParseException) {
                    etDate.error = "Usa el formato AAAA-MM-DD"
                    etDate.requestFocus()
                    return@setOnClickListener
                }
            }

            // Convertir campos opcionales
            val readDate: String? = date.takeIf {
                it.isNotEmpty()
            }

            val bookNotes: String? = notes.takeIf {
                it.isNotEmpty()
            }

            btnSave.isEnabled = false

            lifecycleScope.launch {
                try {
                    val saved = withContext(Dispatchers.IO) {

                        if (bookId == -1L) {

                            // CREAR libro
                            val newBook = BookEntity(
                                userId = userId,
                                title = title,
                                author = author,
                                readDate = readDate,
                                notes = bookNotes
                            )

                            bookDao.insert(newBook)
                            true

                        } else {

                            // ACTUALIZAR libro
                            val rowsUpdated = bookDao.updateBook(
                                bookId = bookId,
                                userId = userId,
                                title = title,
                                author = author,
                                readDate = readDate,
                                notes = bookNotes
                            )

                            rowsUpdated > 0
                        }
                    }

                    if (saved) {
                        Toast.makeText(
                            this@BookFormActivity,
                            "Libro guardado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {
                        Toast.makeText(
                            this@BookFormActivity,
                            "No se pudo guardar el libro",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@BookFormActivity,
                        "Error al guardar el libro",
                        Toast.LENGTH_SHORT
                    ).show()

                } finally {
                    btnSave.isEnabled = true
                }
            }
        }

        // 7. Boton Cancelar
        btnCancel.setOnClickListener {
            finish()
        }

        // 8. Ajustar la pantalla a los bordes
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}
