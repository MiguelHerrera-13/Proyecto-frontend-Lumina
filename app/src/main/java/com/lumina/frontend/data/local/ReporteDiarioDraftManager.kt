package com.lumina.frontend.data.local

import com.lumina.frontend.data.model.ReporteDiarioDraft
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestor en memoria (In-Memory Draft Store) para guardar y recuperar borradores
 * de reportes diarios temporales ante salidas accidentales o navegación hacia atrás.
 */
object ReporteDiarioDraftManager {

    private val borradores = ConcurrentHashMap<Long, ReporteDiarioDraft>()

    fun guardarBorrador(draft: ReporteDiarioDraft) {
        borradores[draft.idTurno] = draft.copy(ultimoGuardadoTimestamp = System.currentTimeMillis())
    }

    fun obtenerBorrador(idTurno: Long): ReporteDiarioDraft? {
        return borradores[idTurno]
    }

    fun tieneBorrador(idTurno: Long): Boolean {
        return borradores.containsKey(idTurno)
    }

    fun descartarBorrador(idTurno: Long) {
        borradores.remove(idTurno)
    }

    fun limpiarTodos() {
        borradores.clear()
    }
}

