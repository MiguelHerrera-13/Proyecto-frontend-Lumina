package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.model.TurnoFiltros
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.data.repository.TurnoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(
        val turnos: List<TurnoResponseDto>,
        val totalSinFiltrar: Int = turnos.size,
        val filtros: TurnoFiltros = TurnoFiltros()
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {
    private val repository = TurnoRepository()

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _filtros = MutableStateFlow(TurnoFiltros())
    val filtros: StateFlow<TurnoFiltros> = _filtros.asStateFlow()

    private var todosLosTurnos: List<TurnoResponseDto> = emptyList()

    fun loadTurnos(pacienteId: Long) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            val result = repository.obtenerTurnos(pacienteId)
            
            result.onSuccess { turnos ->
                todosLosTurnos = turnos
                aplicarFiltrosInternos()
            }.onFailure { error ->
                _uiState.value = DashboardUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Aplica los filtros seleccionados en el ModalBottomSheet de forma reactiva.
     */
    fun aplicarFiltros(nuevosFiltros: TurnoFiltros) {
        _filtros.value = nuevosFiltros
        aplicarFiltrosInternos()
    }

    /**
     * Restablece los filtros a su estado inicial y recarga reactivamente la lista completa.
     */
    fun limpiarFiltros() {
        _filtros.value = TurnoFiltros()
        aplicarFiltrosInternos()
    }

    fun removerEstado(estado: String) {
        val actual = _filtros.value
        _filtros.value = actual.copy(estadosSeleccionados = actual.estadosSeleccionados - estado)
        aplicarFiltrosInternos()
    }

    fun removerFiltroFechas() {
        val actual = _filtros.value
        _filtros.value = actual.copy(fechaDesde = null, fechaHasta = null)
        aplicarFiltrosInternos()
    }

    fun removerFiltroProfesional() {
        val actual = _filtros.value
        _filtros.value = actual.copy(profesionalQuery = "")
        aplicarFiltrosInternos()
    }

    private fun aplicarFiltrosInternos() {
        val filtrados = TurnoFiltros.filtrarTurnos(todosLosTurnos, _filtros.value)
        _uiState.value = DashboardUiState.Success(
            turnos = filtrados,
            totalSinFiltrar = todosLosTurnos.size,
            filtros = _filtros.value
        )
    }
}
