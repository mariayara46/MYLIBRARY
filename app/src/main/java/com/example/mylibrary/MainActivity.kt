package com.example.mylibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mylibrary.data.local.AppDatabase
import com.example.mylibrary.data.repository.UserRepository
import com.example.mylibrary.viewmodel.LoginState
import com.example.mylibrary.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si ya existe una sesión
        val session = getSharedPreferences(
            "mylibrary_session",
            MODE_PRIVATE
        )

        val savedUserId = session.getLong("user_id", -1L)

        if (savedUserId != -1L) {
            startActivity(Intent(this, BooksActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Campos y botones del Login
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoRegister = findViewById<Button>(R.id.btnGoRegister)

        // Conectar Room y Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())

        // Crear LoginViewModel
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return LoginViewModel(repository) as T
            }
        }

        val viewModel = ViewModelProvider(
            this,
            factory
        )[LoginViewModel::class.java]

        // Botón Iniciar sesión
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty()) {
                etEmail.error = "Ingresa tu correo"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        // Observar resultado del Login
        viewModel.state.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    btnLogin.isEnabled = false
                }

                is LoginState.Success -> {
                    session.edit()
                        .putLong("user_id", state.userId)
                        .apply()

                    startActivity(
                        Intent(this, BooksActivity::class.java)
                    )
                    finish()
                }

                is LoginState.Error -> {
                    btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Ir a Registro
        btnGoRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        // Ajustar pantalla
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
