package com.lumina.frontend.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import com.lumina.frontend.data.repository.PatientRepository
import kotlinx.coroutines.launch

data class PatientUiState(
    val isLoading: Boolean = false,
    val patient: PacienteResponseDto? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class PatientsViewModel(
    private val repository: PatientRepository = PatientRepository()
) : ViewModel() {

    var uiState by mutableStateOf(PatientUiState())
        private set

    fun crearPaciente(
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String?
    ) {
        uiState = PatientUiState(isLoading = true)

        viewModelScope.launch {
            val result = repository.crearPaciente(
                PacienteRequestDto(
                    nombreCompleto = nombreCompleto,
                    fechaNacimiento = fechaNacimiento,
                    diagnosticoFase = diagnosticoFase
                )
            )

            uiState = result.fold(
                onSuccess = { paciente ->
                    PatientUiState(
                        patient = paciente,
                        successMessage = "Paciente registrado correctamente"
                    )
                },
                onFailure = {
                    PatientUiState(
                        errorMessage = "No se pudo conectar con el servidor. Intentá nuevamente."
                    )
                }
            )
        }
    }

    fun obtenerPacientePorId(id: Long) {
        uiState = PatientUiState(isLoading = true)

        viewModelScope.launch {
            val result = repository.obtenerPacientePorId(id)

            uiState = result.fold(
                onSuccess = { paciente ->
                    PatientUiState(
                        patient = paciente,
                        successMessage = "Paciente encontrado correctamente"
                    )
                },
                onFailure = {
                    PatientUiState(
                        errorMessage = "No se pudo obtener el paciente."
                    )
                }
            )
        }
    }

    fun limpiarEstado() {
        uiState = PatientUiState()
    }
}