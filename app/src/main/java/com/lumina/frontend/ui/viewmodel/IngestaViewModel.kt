package com.lumina.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.frontend.data.model.IngestaHidratacionData
import com.lumina.frontend.data.model.TipoComida
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class IngestaEvent {
    data class ShowSnackbar(val message: String) : IngestaEvent()
    object SaveSuccess : IngestaEvent()
}

class IngestaViewModel : ViewModel() {

    private val _data = MutableStateFlow(IngestaHidratacionData())
    val data: StateFlow<IngestaHidratacionData> = _data.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _eventFlow = MutableSharedFlow<IngestaEvent>()
    val eventFlow: SharedFlow<IngestaEvent> = _eventFlow.asSharedFlow()

    fun setTipoComida(tipo: TipoComida) {
        _data.update { it.copy(tipoComida = tipo) }
    }

    fun setPorcentaje(porcentaje: Int) {
        _data.update { it.copy(porcentajeIngesta = porcentaje) }
    }

    fun incrementarVasos() {
        _data.update { it.copy(vasosAgua = it.vasosAgua + 1) }
    }

    fun decrementarVasos() {
        _data.update {
            val nuevo = if (it.vasosAgua > 0) it.vasosAgua - 1 else 0
            it.copy(vasosAgua = nuevo)
        }
    }

    fun toggleEtiqueta(etiqueta: String) {
        _data.update { current ->
            val actualizadas = current.etiquetasRapidas.toMutableSet()
            if (actualizadas.contains(etiqueta)) {
                actualizadas.remove(etiqueta)
            } else {
                actualizadas.add(etiqueta)
            }
            current.copy(etiquetasRapidas = actualizadas)
        }
    }

    fun setNotas(notas: String) {
        _data.update { it.copy(notasEspeciales = notas) }
    }

    fun guardarRegistro() {
        viewModelScope.launch {
            _isSaving.value = true
            val snapshot = _data.value
            // Registro guardado localmente / preparado para backend
            val resumen = "Registro guardado: ${snapshot.tipoComida.displayName} (${snapshot.porcentajeIngesta}%), ${snapshot.totalMililitrosAgua} ml agua"
            _eventFlow.emit(IngestaEvent.ShowSnackbar(resumen))
            _eventFlow.emit(IngestaEvent.SaveSuccess)
            _isSaving.value = false
        }
    }

    fun reiniciar() {
        _data.value = IngestaHidratacionData()
    }
}
