
package com.example.mylibrary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class RegisterState {
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableLiveData<RegisterState>()
    val state: LiveData<RegisterState> = _state

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        if (_state.value is RegisterState.Loading) return

        _state.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.registerUser(
                        name,
                        email,
                        password
                    )
                }

                _state.value = RegisterState.Success

            } catch (e: IllegalArgumentException) {
                _state.value = RegisterState.Error(
                    e.message ?: "Datos inválidos"
                )

            } catch (e: Exception) {
                _state.value = RegisterState.Error(
                    "No se pudo crear la cuenta"
                )
            }
        }
    }
}
