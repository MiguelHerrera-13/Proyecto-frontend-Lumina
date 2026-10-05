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
                    _registerState.value = RegisterState.Success("Usuario registrado exitosamente")
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
    
    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
