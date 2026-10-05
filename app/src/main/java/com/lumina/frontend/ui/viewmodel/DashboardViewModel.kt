package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.data.repository.TurnoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definimos nuestros estados (Respondemos a la pregunta académica)
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val turnos: List<TurnoResponseDto>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {
    private val repository = TurnoRepository()

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // 2. Función para cargar los datos
    fun loadTurnos(pacienteId: Long) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading // Estado inicial
            val result = repository.obtenerTurnos(pacienteId)
            
            result.onSuccess { turnos ->
                _uiState.value = DashboardUiState.Success(turnos)
            }.onFailure { error ->
                _uiState.value = DashboardUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }
}
