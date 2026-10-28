package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.dto.HistorialResponseDto
import com.lumina.frontend.data.remote.dto.MedicamentoResponseDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.data.repository.PatientDetailRepository
import com.lumina.frontend.data.repository.PatientRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PatientDetailTab(val title: String) {
    GENERAL("General"),
    TURNOS("Turnos"),
    MEDICAMENTOS("Medicamentos"),
    HISTORIAL("Historial")
}

data class PatientDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: PatientDetailTab = PatientDetailTab.GENERAL,
    val paciente: PacienteResponseDto? = null,
    val turnos: List<TurnoResponseDto> = emptyList(),
    val medicamentos: List<MedicamentoResponseDto> = emptyList(),
    val historial: List<HistorialResponseDto> = emptyList()
)

class PatientDetailViewModel(
    private val patientRepository: PatientRepository = PatientRepository(),
    private val detailRepository: PatientDetailRepository = PatientDetailRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientDetailUiState(isLoading = true))
    val uiState: StateFlow<PatientDetailUiState> = _uiState.asStateFlow()

    fun selectTab(tab: PatientDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun cargarDetallePaciente(pacienteId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Carga asíncrona coordinada en paralelo para todas las pestañas
            val pacienteDeferred = async { patientRepository.obtenerPacientePorId(pacienteId) }
            val turnosDeferred = async { detailRepository.getTurnosPaciente(pacienteId) }
            val medicamentosDeferred = async { detailRepository.getMedicamentosPaciente(pacienteId) }
            val historialDeferred = async { detailRepository.getHistorialPaciente(pacienteId) }

            val resPaciente = pacienteDeferred.await()
            val resTurnos = turnosDeferred.await()
            val resMedicamentos = medicamentosDeferred.await()
            val resHistorial = historialDeferred.await()

            if (resPaciente.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        paciente = resPaciente.getOrNull(),
                        turnos = resTurnos.getOrDefault(emptyList()),
                        medicamentos = resMedicamentos.getOrDefault(emptyList()),
                        historial = resHistorial.getOrDefault(emptyList()),
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = resPaciente.exceptionOrNull()?.localizedMessage
                            ?: "No se pudo cargar la información del paciente"
                    )
                }
            }
        }
    }
}

