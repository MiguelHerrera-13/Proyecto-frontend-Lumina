package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.model.ReporteDiarioDraft
import com.lumina.frontend.data.model.ReporteDiarioPaso
import com.lumina.frontend.data.model.ReporteGeneralData
import com.lumina.frontend.data.model.ReporteHigieneData
import com.lumina.frontend.data.model.ReporteIncidentesData
import com.lumina.frontend.data.model.ReporteIngestaData
import com.lumina.frontend.data.repository.ReporteDiarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReporteDiarioUiState(
    val idTurno: Long = 1L,
    val pasoActual: ReporteDiarioPaso = ReporteDiarioPaso.GENERAL,
    val general: ReporteGeneralData = ReporteGeneralData(),
    val ingesta: ReporteIngestaData = ReporteIngestaData(),
    val higiene: ReporteHigieneData = ReporteHigieneData(),
    val incidentes: ReporteIncidentesData = ReporteIncidentesData(),
    val porcentajeProgreso: Float = 0.25f,
    val borradorGuardadoEnMemoria: Boolean = false,
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val mensajeFeedback: String? = null,
    val error: String? = null
)

class ReporteDiarioViewModel(
    private val repository: ReporteDiarioRepository = ReporteDiarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReporteDiarioUiState())
    val uiState: StateFlow<ReporteDiarioUiState> = _uiState.asStateFlow()

    fun inicializarTurno(idTurno: Long) {
        val borradorExistente = repository.obtenerBorrador(idTurno)
        if (borradorExistente != null) {
            _uiState.update {
                it.copy(
                    idTurno = idTurno,
                    general = borradorExistente.general,
                    ingesta = borradorExistente.ingesta,
                    higiene = borradorExistente.higiene,
                    incidentes = borradorExistente.incidentes,
                    borradorGuardadoEnMemoria = true,
                    porcentajeProgreso = calcularProgreso(
                        borradorExistente.general,
                        borradorExistente.ingesta,
                        borradorExistente.higiene,
                        borradorExistente.incidentes
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    idTurno = idTurno,
                    porcentajeProgreso = calcularProgreso(it.general, it.ingesta, it.higiene, it.incidentes)
                )
            }
        }
    }

    fun irAPaso(paso: ReporteDiarioPaso) {
        _uiState.update { it.copy(pasoActual = paso) }
    }

    fun avanzarPaso() {
        val siguiente = when (_uiState.value.pasoActual) {
            ReporteDiarioPaso.GENERAL -> ReporteDiarioPaso.INGESTA
            ReporteDiarioPaso.INGESTA -> ReporteDiarioPaso.HIGIENE
            ReporteDiarioPaso.HIGIENE -> ReporteDiarioPaso.INCIDENTES
            ReporteDiarioPaso.INCIDENTES -> ReporteDiarioPaso.INCIDENTES
        }
        _uiState.update { it.copy(pasoActual = siguiente) }
    }

    fun retrocederPaso() {
        val anterior = when (_uiState.value.pasoActual) {
            ReporteDiarioPaso.GENERAL -> ReporteDiarioPaso.GENERAL
            ReporteDiarioPaso.INGESTA -> ReporteDiarioPaso.GENERAL
            ReporteDiarioPaso.HIGIENE -> ReporteDiarioPaso.INGESTA
            ReporteDiarioPaso.INCIDENTES -> ReporteDiarioPaso.HIGIENE
        }
        _uiState.update { it.copy(pasoActual = anterior) }
    }

    // Actualizaciones por sección con auto-guardado en borrador
    fun updateGeneral(general: ReporteGeneralData) {
        _uiState.update { state ->
            val updated = state.copy(
                general = general,
                borradorGuardadoEnMemoria = true,
                porcentajeProgreso = calcularProgreso(general, state.ingesta, state.higiene, state.incidentes)
            )
            autoGuardarBorrador(updated)
            updated
        }
    }

    fun updateIngesta(ingesta: ReporteIngestaData) {
        _uiState.update { state ->
            val updated = state.copy(
                ingesta = ingesta,
                borradorGuardadoEnMemoria = true,
                porcentajeProgreso = calcularProgreso(state.general, ingesta, state.higiene, state.incidentes)
            )
            autoGuardarBorrador(updated)
            updated
        }
    }

    fun updateHigiene(higiene: ReporteHigieneData) {
        _uiState.update { state ->
            val updated = state.copy(
                higiene = higiene,
                borradorGuardadoEnMemoria = true,
                porcentajeProgreso = calcularProgreso(state.general, state.ingesta, higiene, state.incidentes)
            )
            autoGuardarBorrador(updated)
            updated
        }
    }

    fun updateIncidentes(incidentes: ReporteIncidentesData) {
        _uiState.update { state ->
            val updated = state.copy(
                incidentes = incidentes,
                borradorGuardadoEnMemoria = true,
                porcentajeProgreso = calcularProgreso(state.general, state.ingesta, state.higiene, incidentes)
            )
            autoGuardarBorrador(updated)
            updated
        }
    }

    private fun autoGuardarBorrador(state: ReporteDiarioUiState) {
        val draft = ReporteDiarioDraft(
            idTurno = state.idTurno,
            general = state.general,
            ingesta = state.ingesta,
            higiene = state.higiene,
            incidentes = state.incidentes
        )
        repository.guardarBorrador(draft)
    }

    fun descartarBorrador() {
        repository.descartarBorrador(_uiState.value.idTurno)
        _uiState.update {
            ReporteDiarioUiState(
                idTurno = it.idTurno,
                borradorGuardadoEnMemoria = false,
                mensajeFeedback = "Borrador descartado"
            )
        }
    }

    fun guardarBorradorManual() {
        autoGuardarBorrador(_uiState.value)
        _uiState.update {
            it.copy(
                borradorGuardadoEnMemoria = true,
                mensajeFeedback = "Borrador guardado exitosamente en memoria"
            )
        }
    }

    fun finalizarReporte() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val draft = ReporteDiarioDraft(
                idTurno = _uiState.value.idTurno,
                general = _uiState.value.general,
                ingesta = _uiState.value.ingesta,
                higiene = _uiState.value.higiene,
                incidentes = _uiState.value.incidentes
            )
            val result = repository.enviarReporte(draft)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoading = false,
                        guardadoExitoso = true,
                        borradorGuardadoEnMemoria = false,
                        mensajeFeedback = "Reporte diario registrado con éxito"
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo sincronizar el reporte con el servidor"
                    )
                }
            }
        }
    }

    private fun calcularProgreso(
        general: ReporteGeneralData,
        ingesta: ReporteIngestaData,
        higiene: ReporteHigieneData,
        incidentes: ReporteIncidentesData
    ): Float {
        var puntaje = 0f
        // Paso 1: General (0.25)
        if (general.temperatura.isNotBlank() && general.presionArterial.isNotBlank()) puntaje += 0.25f
        // Paso 2: Ingesta (0.25)
        if (ingesta.aguaMlConsumida > 0) puntaje += 0.25f
        // Paso 3: Higiene (0.25)
        if (higiene.cambioPanalesVeces >= 0) puntaje += 0.25f
        // Paso 4: Incidentes (0.25)
        if (!incidentes.huboIncidentes || incidentes.descripcionIncidente.isNotBlank()) puntaje += 0.25f

        return puntaje.coerceIn(0.10f, 1.0f)
    }
}

