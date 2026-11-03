package com.example.mylibrary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val userId: Long) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableLiveData<LoginState>()
    val state: LiveData<LoginState> = _state

    fun login(email: String, password: String) {

        if (_state.value is LoginState.Loading) return

        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error(
                "Ingresa tu correo y contraseña"
            )
            return
        }

        _state.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    repository.login(email, password)
                }

                if (user != null) {
                    _state.value = LoginState.Success(user.id)
                } else {
                    _state.value = LoginState.Error(
                        "Correo o contraseña incorrectos"
                    )
                }

            } catch (e: Exception) {
                _state.value = LoginState.Error(
                    "No se pudo iniciar sesión"
                )
            }
        }
    }
}
