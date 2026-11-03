package com.example.mylibrary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylibrary.adapter.BookAdapter
import com.example.mylibrary.data.local.AppDatabase
import kotlinx.coroutines.launch

class BooksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_books)

        // Recuperar el usuario que inició sesión
        val session = getSharedPreferences(
            "mylibrary_session",
            MODE_PRIVATE
        )

        val userId = session.getLong("user_id", -1L)

        // Si no hay sesión, regresar al Login
        if (userId == -1L) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Obtener los elementos de la pantalla
        val rvBooks = findViewById<RecyclerView>(R.id.rvBooks)
        val txtEmpty = findViewById<TextView>(R.id.txtEmpty)
        val btnAddBook = findViewById<Button>(R.id.btnAddBook)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Configurar RecyclerView
        val adapter = BookAdapter { book ->
            val intent = Intent(
                this@BooksActivity,
                BookDetailActivity::class.java
            )
            intent.putExtra("book_id", book.id)
            startActivity(intent)
        }

        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = adapter

        // Conectar con Room
        val database = AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {
            database.bookDao().getBooks(userId).collect { books ->

                adapter.submitList(books)

                txtEmpty.visibility =
                    if (books.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // Botón Agregar libro

        // Abrir formulario para agregar un libro
        btnAddBook.setOnClickListener {
            val intent = Intent(
                this,
                BookFormActivity::class.java
            )
            startActivity(intent)
        }


        // Botón Cerrar sesión
        btnLogout.setOnClickListener {
            session.edit().remove("user_id").apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        // Ajustar los bordes de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}
