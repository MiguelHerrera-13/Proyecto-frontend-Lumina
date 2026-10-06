package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.dto.AlertaResponseDto
import com.lumina.frontend.data.repository.AlertaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AlertasUiState {
    object Loading : AlertasUiState
    data class Success(val alertas: List<AlertaResponseDto>) : AlertasUiState
    data class Error(val message: String) : AlertasUiState
}

class DoctorDashboardViewModel : ViewModel() {
    private val repository = AlertaRepository()

    private val _uiState = MutableStateFlow<AlertasUiState>(AlertasUiState.Loading)
    val uiState: StateFlow<AlertasUiState> = _uiState.asStateFlow()

    fun cargarAlertas(medicoId: Long = 1L) {
        viewModelScope.launch {
            _uiState.value = AlertasUiState.Loading
            val result = repository.obtenerAlertasMedico(medicoId)
            result.onSuccess { alertas ->
                _uiState.value = AlertasUiState.Success(alertas)
            }.onFailure { error ->
                _uiState.value = AlertasUiState.Error(error.message ?: "Error al cargar las alertas clínicas")
            }
        }
    }
}
