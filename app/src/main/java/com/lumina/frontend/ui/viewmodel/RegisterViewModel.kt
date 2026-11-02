package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.UsuarioRequestDto
import com.lumina.frontend.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Activated(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val repository = UsuarioRepository(RetrofitClient.usuarioApiService)

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun registerUser(nombre: String, email: String, contrasena: String, rol: String) {
        if (!email.contains("@")) {
            _registerState.value = RegisterState.Error("El correo electrónico no es válido")
            return
        }
        
        if (nombre.isBlank() || contrasena.isBlank() || rol.isBlank()) {
            _registerState.value = RegisterState.Error("Todos los campos son obligatorios")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val request = UsuarioRequestDto(nombre, email, contrasena, rol)
                val response = repository.registrarUsuario(request)

                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success("Solicitud de registro enviada. Revisa tu correo de cuidadores.ies@gmail.com")
                } else if (response.code() == 409) {
                    _registerState.value = RegisterState.Error("El correo electrónico ya existe")
                } else {
                    _registerState.value = RegisterState.Error("Error al registrar: ${response.code()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun verificarCodigo(token: String) {
        if (token.isBlank()) {
            _registerState.value = RegisterState.Error("Introduce el código recibido")
            return
        }
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = RetrofitClient.authApiService.verificarCodigo(mapOf("token" to token.trim().uppercase()))
                if (response.isSuccessful) {
                    val msg = response.body()?.get("mensaje") ?: "¡Cuenta activada con éxito!"
                    _registerState.value = RegisterState.Activated(msg)
                } else {
                    _registerState.value = RegisterState.Error("Código o token inválido o expirado")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
    
    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
