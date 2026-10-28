package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import com.lumina.frontend.data.repository.PatientRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PatientStatusFilter {
    ALL,
    ACTIVE,
    INACTIVE
}

data class PatientUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: PatientStatusFilter = PatientStatusFilter.ALL,
    val allPatients: List<PacienteResponseDto> = emptyList(),
    val filteredPatients: List<PacienteResponseDto> = emptyList(),
    val patient: PacienteResponseDto? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class PatientsViewModel(
    private val repository: PatientRepository = PatientRepository()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(PatientStatusFilter.ALL)
    val selectedFilter: StateFlow<PatientStatusFilter> = _selectedFilter.asStateFlow()

    private val _allPatients = MutableStateFlow<List<PacienteResponseDto>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _successMessage = MutableStateFlow<String?>(null)
    private val _singlePatient = MutableStateFlow<PacienteResponseDto?>(null)

    // Pipeline reactivo con debounce de 400ms para evitar llamadas o filtros excesivos al tipear
    val uiState: StateFlow<PatientUiState> = combine(
        combine(
            _searchQuery.debounce(400L).distinctUntilChanged(),
            _selectedFilter,
            _allPatients
        ) { query, filter, patients ->
            Triple(query, filter, patients)
        },
        _isLoading,
        _errorMessage,
        _successMessage,
        _singlePatient
    ) { (query, filter, patients), isLoading, errorMsg, successMsg, singlePatient ->
        val filtered = patients.filter { p ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                p.nombreCompleto.contains(query, ignoreCase = true) ||
                p.idPaciente.toString().contains(query)
            }

            val matchesFilter = when (filter) {
                PatientStatusFilter.ALL -> true
                PatientStatusFilter.ACTIVE -> p.activo
                PatientStatusFilter.INACTIVE -> !p.activo
            }

            matchesQuery && matchesFilter
        }

        PatientUiState(
            isLoading = isLoading,
            searchQuery = query,
            selectedFilter = filter,
            allPatients = patients,
            filteredPatients = filtered,
            patient = singlePatient,
            successMessage = successMsg,
            errorMessage = errorMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatientUiState(isLoading = true)
    )

    init {
        cargarPacientes()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: PatientStatusFilter) {
        _selectedFilter.value = filter
    }

    fun cargarPacientes() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.obtenerPacientes()
            _isLoading.value = false
            result.fold(
                onSuccess = { lista ->
                    _allPatients.value = lista
                },
                onFailure = { error ->
                    _errorMessage.value = "Error al cargar pacientes: ${error.message}"
                }
            )
        }
    }

    fun crearPaciente(
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String?
    ) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.crearPaciente(
                PacienteRequestDto(
                    nombreCompleto = nombreCompleto,
                    fechaNacimiento = fechaNacimiento,
                    diagnosticoFase = diagnosticoFase
                )
            )

            _isLoading.value = false
            result.fold(
                onSuccess = { paciente ->
                    _successMessage.value = "Paciente registrado correctamente"
                    cargarPacientes()
                },
                onFailure = {
                    _errorMessage.value = "No se pudo registrar el paciente. Intente nuevamente."
                }
            )
        }
    }

    fun obtenerPacientePorId(id: Long) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.obtenerPacientePorId(id)
            _isLoading.value = false

            result.fold(
                onSuccess = { paciente ->
                    _singlePatient.value = paciente
                    _successMessage.value = "Paciente encontrado"
                },
                onFailure = {
                    _errorMessage.value = "No se pudo obtener el paciente."
                }
            )
        }
    }

    fun limpiarEstado() {
        _errorMessage.value = null
        _successMessage.value = null
        _singlePatient.value = null
    }
}