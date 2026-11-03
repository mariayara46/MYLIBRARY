
package com.example.mylibrary.data.repository

import com.example.mylibrary.data.local.UserDao
import com.example.mylibrary.data.local.UserEntity
import com.example.mylibrary.security.PasswordUtils
import java.util.Locale

class UserRepository(
    private val userDao: UserDao
) {

    // REGISTRAR USUARIO
    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Long {

        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase(Locale.ROOT)

        require(cleanName.isNotEmpty()) {
            "El nombre es obligatorio"
        }

        require(android.util.Patterns.EMAIL_ADDRESS
            .matcher(cleanEmail).matches()) {
            "Ingresa un correo válido"
        }

        require(password.length >= 6) {
            "La contraseña debe tener mínimo 6 caracteres"
        }

        require(userDao.getByEmail(cleanEmail) == null) {
            "Este correo ya está registrado"
        }

        val salt = PasswordUtils.generateSalt()

        val passwordHash = PasswordUtils.hashPassword(
            password,
            salt
        )

        val newUser = UserEntity(
            name = cleanName,
            email = cleanEmail,
            passwordHash = passwordHash,
            passwordSalt = salt
        )

        return userDao.insert(newUser)
    }

    // BUSCAR USUARIO POR CORREO
    suspend fun findUserByEmail(
        email: String
    ): UserEntity? {

        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        return userDao.getByEmail(cleanEmail)
    }

    // VERIFICAR CREDENCIALES DEL LOGIN
    suspend fun login(
        email: String,
        password: String
    ): UserEntity? {

        val user = findUserByEmail(email)
            ?: return null

        val isValid = PasswordUtils.verifyPassword(
            password,
            user.passwordSalt,
            user.passwordHash
        )

        return if (isValid) user else null
    }
}
