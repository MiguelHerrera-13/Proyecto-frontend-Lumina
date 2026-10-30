package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.ActualizarUsuarioRequestDto
import com.lumina.frontend.data.remote.dto.UsuarioPerfilDto
import com.lumina.frontend.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val perfil: UsuarioPerfilDto) : ProfileUiState()
    data class Error(val mensaje: String) : ProfileUiState()
}

sealed class GuardarEstado {
    object Idle : GuardarEstado()
    object Loading : GuardarEstado()
    data class Exito(val mensaje: String) : GuardarEstado()
    data class Error(val mensaje: String) : GuardarEstado()
}

class ProfileViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: UsuarioRepository = UsuarioRepository(RetrofitClient.usuarioApiService)
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Campos editables guardados en SavedStateHandle para sobrevivir rotación Y muerte del proceso
    var nombreEditado: String
        get() = savedStateHandle.get<String>("nombreEditado") ?: ""
        set(value) { savedStateHandle["nombreEditado"] = value }

    var telefonoEditado: String
        get() = savedStateHandle.get<String>("telefonoEditado") ?: ""
        set(value) { savedStateHandle["telefonoEditado"] = value }

    private val _nombreFlow = MutableStateFlow(nombreEditado)
    val nombreFlow: StateFlow<String> = _nombreFlow.asStateFlow()

    private val _telefonoFlow = MutableStateFlow(telefonoEditado)
    val telefonoFlow: StateFlow<String> = _telefonoFlow.asStateFlow()

    private val _guardarEstado = MutableStateFlow<GuardarEstado>(GuardarEstado.Idle)
    val guardarEstado: StateFlow<GuardarEstado> = _guardarEstado.asStateFlow()

    // Errores de validación
    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre.asStateFlow()

    private val _errorTelefono = MutableStateFlow<String?>(null)
    val errorTelefono: StateFlow<String?> = _errorTelefono.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val response = repository.obtenerPerfil()
                if (response.isSuccessful && response.body() != null) {
                    val perfil = response.body()!!
                    _uiState.value = ProfileUiState.Success(perfil)
                    // Solo inicializar si no hay valores guardados (primera carga)
                    if (nombreEditado.isBlank()) {
                        onNombreChange(perfil.nombre)
                    }
                    if (telefonoEditado.isBlank()) {
                        onTelefonoChange(perfil.telefono ?: "")
                    }
                } else {
                    _uiState.value = ProfileUiState.Error("Error ${response.code()}: no se pudo cargar el perfil")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Sin conexión: ${e.message}")
            }
        }
    }

    fun onNombreChange(value: String) {
        nombreEditado = value
        _nombreFlow.value = value
        _errorNombre.value = if (value.isBlank()) "El nombre no puede estar vacío" else null
    }

    fun onTelefonoChange(value: String) {
        telefonoEditado = value
        _telefonoFlow.value = value
        _errorTelefono.value = if (value.isNotBlank() && !value.matches(Regex("^[+]?[0-9\\s\\-]{7,15}$"))) {
            "Teléfono inválido"
        } else null
    }

    fun guardarCambios() {
        if (!validar()) return

        val estadoActual = _uiState.value
        if (estadoActual !is ProfileUiState.Success) return
        val id = estadoActual.perfil.idUsuario

        viewModelScope.launch {
            _guardarEstado.value = GuardarEstado.Loading
            try {
                val request = ActualizarUsuarioRequestDto(
                    nombre = nombreEditado.trim(),
                    telefono = telefonoEditado.trim().ifBlank { null }
                )
                val response = repository.actualizarUsuario(id, request)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                    _guardarEstado.value = GuardarEstado.Exito("Datos actualizados correctamente")
                } else {
                    _guardarEstado.value = GuardarEstado.Error("Error ${response.code()}: no se pudo actualizar")
                }
            } catch (e: Exception) {
                _guardarEstado.value = GuardarEstado.Error("Sin conexión: ${e.message}")
            }
        }
    }

    fun resetGuardarEstado() {
        _guardarEstado.value = GuardarEstado.Idle
    }

    private fun validar(): Boolean {
        var valido = true
        if (nombreEditado.isBlank()) {
            _errorNombre.value = "El nombre no puede estar vacío"
            valido = false
        }
        if (telefonoEditado.isNotBlank() && !telefonoEditado.matches(Regex("^[+]?[0-9\\s\\-]{7,15}$"))) {
            _errorTelefono.value = "Teléfono inválido"
            valido = false
        }
        return valido
    }
}
