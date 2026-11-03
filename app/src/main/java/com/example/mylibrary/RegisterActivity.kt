package com.example.mylibrary

import android.os.Bundle
import android.util.Patterns
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
import com.example.mylibrary.viewmodel.RegisterState
import com.example.mylibrary.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Campos del formulario
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // Botones
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoLogin = findViewById<Button>(R.id.btnGoLogin)

        // Conectar Room con Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())

        // Crear ViewModel con su Repository
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return RegisterViewModel(repository) as T
            }
        }

        val viewModel = ViewModelProvider(
            this,
            factory
        )[RegisterViewModel::class.java]

        // Registrar usuario
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isEmpty()) {
                etName.error = "Ingresa tu nombre"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Ingresa un correo válido"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            viewModel.register(name, email, password)
        }

        // Observar el resultado del registro
        viewModel.state.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    btnRegister.isEnabled = false
                }

                is RegisterState.Success -> {
                    Toast.makeText(
                        this,
                        "Cuenta creada correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                is RegisterState.Error -> {
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Regresar al Login
        btnGoLogin.setOnClickListener {
            finish()
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
