
package com.example.mylibrary.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    private const val ITERATIONS = 120000
    private const val KEY_LENGTH = 256

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        return Base64.encodeToString(
            salt,
            Base64.NO_WRAP
        )
    }

    fun hashPassword(
        password: String,
        salt: String
    ): String {
        val saltBytes = Base64.decode(
            salt,
            Base64.NO_WRAP
        )

        val spec = PBEKeySpec(
            password.toCharArray(),
            saltBytes,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            val factory = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

            val hash = factory.generateSecret(spec).encoded

            Base64.encodeToString(
                hash,
                Base64.NO_WRAP
            )
        } finally {
            spec.clearPassword()
        }
    }

    fun verifyPassword(
        password: String,
        salt: String,
        storedHash: String
    ): Boolean {
        val calculatedHash = hashPassword(password, salt)

        return MessageDigest.isEqual(
            Base64.decode(calculatedHash, Base64.NO_WRAP),
            Base64.decode(storedHash, Base64.NO_WRAP)
        )
    }
}
