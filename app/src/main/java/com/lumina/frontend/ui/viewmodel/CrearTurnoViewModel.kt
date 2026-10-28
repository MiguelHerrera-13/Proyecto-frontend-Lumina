package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.local.FakePatientDataSource
import com.lumina.frontend.data.remote.dto.CrearTurnoRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.data.repository.ConflictoTurnoException
import com.lumina.frontend.data.repository.PatientRepository
import com.lumina.frontend.data.repository.TurnoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed interface CrearTurnoUiState {
    object Idle : CrearTurnoUiState
    object Loading : CrearTurnoUiState
    data class Success(val turno: TurnoResponseDto) : CrearTurnoUiState
    data class ConflictError(
        val mensaje: String,
        val sugerencia: String = "Por favor, modifica el rango de horario o elige otro profesional."
    ) : CrearTurnoUiState
    data class Error(val mensaje: String) : CrearTurnoUiState
}

data class CrearTurnoFormState(
    val pacienteSeleccionado: PacienteResponseDto? = null,
    val profesionalSeleccionado: String = "",
    val fecha: LocalDate = LocalDate.now().plusDays(1),
    val horaInicio: LocalTime = LocalTime.of(9, 0),
    val horaFin: LocalTime = LocalTime.of(10, 0),
    val motivo: String = ""
) {
    /**
     * Criterio de Aceptación: Validación de que la hora de inicio sea anterior a la de finalización.
     */
    val isHoraValida: Boolean
        get() = horaInicio.isBefore(horaFin)

    val mensajeErrorHora: String?
        get() = if (!isHoraValida) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            "La hora de inicio (${horaInicio.format(formatter)}) debe ser anterior a la hora de fin (${horaFin.format(formatter)})."
        } else null

    val isFormularioValido: Boolean
        get() = pacienteSeleccionado != null &&
                profesionalSeleccionado.isNotBlank() &&
                isHoraValida &&
                motivo.trim().length >= 4
}

class CrearTurnoViewModel(
    private val turnoRepository: TurnoRepository = TurnoRepository(),
    private val patientRepository: PatientRepository = PatientRepository()
) : ViewModel() {

    val profesionalesDisponibles = listOf(
        "Dra. Martínez (Geriatría y Clínica)",
        "Dr. Alejandro Gómez (Médico General)",
        "Dr. Sánchez (Neurología)",
        "Lic. Fernández (Kinesiología)",
        "Lic. Valenzuela (Nutrición)",
        "Enf. Cuidador Principal (Atención y Monitoreo)"
    )

    private val _pacientes = MutableStateFlow<List<PacienteResponseDto>>(emptyList())
    val pacientes: StateFlow<List<PacienteResponseDto>> = _pacientes.asStateFlow()

    private val _formState = MutableStateFlow(CrearTurnoFormState())
    val formState: StateFlow<CrearTurnoFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<CrearTurnoUiState>(CrearTurnoUiState.Idle)
    val uiState: StateFlow<CrearTurnoUiState> = _uiState.asStateFlow()

    init {
        cargarPacientes()
        // Inicializar con el primer profesional de la lista
        _formState.value = _formState.value.copy(
            profesionalSeleccionado = profesionalesDisponibles.first()
        )
    }

    fun cargarPacientes() {
        viewModelScope.launch {
            val result = patientRepository.obtenerPacientes()
            result.onSuccess { lista ->
                val pacientesCargados = if (lista.isNotEmpty()) lista else FakePatientDataSource.getPatientsDto()
                _pacientes.value = pacientesCargados
                if (_formState.value.pacienteSeleccionado == null && pacientesCargados.isNotEmpty()) {
                    _formState.value = _formState.value.copy(pacienteSeleccionado = pacientesCargados.first())
                }
            }.onFailure {
                val fallback = FakePatientDataSource.getPatientsDto()
                _pacientes.value = fallback
                if (_formState.value.pacienteSeleccionado == null && fallback.isNotEmpty()) {
                    _formState.value = _formState.value.copy(pacienteSeleccionado = fallback.first())
                }
            }
        }
    }

    fun onPacienteSelected(paciente: PacienteResponseDto) {
        _formState.value = _formState.value.copy(pacienteSeleccionado = paciente)
        limpiarError()
    }

    fun onProfesionalSelected(profesional: String) {
        _formState.value = _formState.value.copy(profesionalSeleccionado = profesional)
        limpiarError()
    }

    fun onFechaSelected(fecha: LocalDate) {
        _formState.value = _formState.value.copy(fecha = fecha)
        limpiarError()
    }

    fun onHoraInicioSelected(hora: LocalTime) {
        _formState.value = _formState.value.copy(horaInicio = hora)
        limpiarError()
    }

    fun onHoraFinSelected(hora: LocalTime) {
        _formState.value = _formState.value.copy(horaFin = hora)
        limpiarError()
    }

    fun onMotivoChanged(nuevoMotivo: String) {
        _formState.value = _formState.value.copy(motivo = nuevoMotivo)
        limpiarError()
    }

    fun limpiarError() {
        if (_uiState.value is CrearTurnoUiState.ConflictError || _uiState.value is CrearTurnoUiState.Error) {
            _uiState.value = CrearTurnoUiState.Idle
        }
    }

    /**
     * Envia la solicitud de agendamiento del turno al backend (POST /api/turnos).
     */
    fun agendarTurno() {
        val actual = _formState.value

        // Validación previa estricta
        if (!actual.isHoraValida) {
            _uiState.value = CrearTurnoUiState.Error(
                actual.mensajeErrorHora ?: "La hora de inicio debe ser anterior a la hora de finalización."
            )
            return
        }

        val paciente = actual.pacienteSeleccionado
        if (paciente == null) {
            _uiState.value = CrearTurnoUiState.Error("Debe seleccionar un paciente para agendar el turno.")
            return
        }

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val request = CrearTurnoRequestDto(
            pacienteId = paciente.idPaciente,
            nombreMedico = actual.profesionalSeleccionado,
            fecha = actual.fecha.format(dateFormatter),
            horaInicio = actual.horaInicio.format(timeFormatter),
            horaFin = actual.horaFin.format(timeFormatter),
            motivo = actual.motivo.trim().ifEmpty { "Consulta de control general y seguimiento" }
        )

        viewModelScope.launch {
            _uiState.value = CrearTurnoUiState.Loading
            val resultado = turnoRepository.crearTurno(request)

            resultado.onSuccess { turnoCreado ->
                _uiState.value = CrearTurnoUiState.Success(turnoCreado)
            }.onFailure { exception ->
                if (exception is ConflictoTurnoException) {
                    // Manejo especializado del error 409 Conflict
                    _uiState.value = CrearTurnoUiState.ConflictError(
                        mensaje = exception.message,
                        sugerencia = exception.sugerencia
                    )
                } else {
                    _uiState.value = CrearTurnoUiState.Error(
                        exception.message ?: "Ocurrió un error al agendar el turno. Inténtalo nuevamente."
                    )
                }
            }
        }
    }
}
