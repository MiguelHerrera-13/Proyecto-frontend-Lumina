package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val rol: String, val email: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, contrasena: String) {
        if (email.isBlank()) {
            _loginState.value = LoginState.Error("El correo electrónico es obligatorio")
            return
        }
        if (!email.contains("@")) {
            _loginState.value = LoginState.Error("Ingresá un correo electrónico válido")
            return
        }
        if (contrasena.isBlank()) {
            _loginState.value = LoginState.Error("La contraseña es obligatoria")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = authRepository.login(email, contrasena)
            result.onSuccess { authData ->
                _loginState.value = LoginState.Success(rol = authData.rol, email = authData.email)
            }.onFailure { error ->
                _loginState.value = LoginState.Error(error.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
