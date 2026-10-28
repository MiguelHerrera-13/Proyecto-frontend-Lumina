package com.lumina.frontend.data.model

import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Modelo de datos que encapsula los criterios de filtrado avanzado de turnos médicos.
 *
 * Cumple con los criterios de la Issue #32 (Frontend #24):
 * - Filtrado por estado múltiple (PROGRAMADO, PENDIENTE, COMPLETADO, CANCELADO, REPROGRAMADO).
 * - Filtrado por profesional / médico asignado.
 * - Filtrado por rango de fechas (fecha desde / fecha hasta).
 */
data class TurnoFiltros(
    val estadosSeleccionados: Set<String> = emptySet(),
    val profesionalQuery: String = "",
    val fechaDesde: String? = null,
    val fechaHasta: String? = null
) {
    val tieneFiltrosActivos: Boolean
        get() = estadosSeleccionados.isNotEmpty() ||
                profesionalQuery.isNotBlank() ||
                !fechaDesde.isNullOrBlank() ||
                !fechaHasta.isNullOrBlank()

    val cantidadFiltrosActivos: Int
        get() {
            var count = 0
            if (estadosSeleccionados.isNotEmpty()) count += estadosSeleccionados.size
            if (profesionalQuery.isNotBlank()) count += 1
            if (!fechaDesde.isNullOrBlank()) count += 1
            if (!fechaHasta.isNullOrBlank()) count += 1
            return count
        }

    companion object {
        val ESTADOS_DISPONIBLES = listOf(
            "PROGRAMADO",
            "PENDIENTE",
            "COMPLETADO",
            "REPROGRAMADO",
            "CANCELADO"
        )

        /**
         * Aplica los filtros sobre la lista completa de turnos de manera reactiva y pura.
         */
        fun filtrarTurnos(
            turnos: List<TurnoResponseDto>,
            filtros: TurnoFiltros
        ): List<TurnoResponseDto> {
            if (!filtros.tieneFiltrosActivos) {
                return turnos
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val fechaDesdeDate = filtros.fechaDesde?.let {
                try { dateFormat.parse(it) } catch (_: Exception) { null }
            }
            val fechaHastaDate = filtros.fechaHasta?.let {
                try { dateFormat.parse(it) } catch (_: Exception) { null }
            }

            return turnos.filter { turno ->
                // 1. Filtro por Estado Múltiple
                val cumpleEstado = if (filtros.estadosSeleccionados.isNotEmpty()) {
                    val estadoTurno = turno.estado?.uppercase() ?: "PROGRAMADO"
                    filtros.estadosSeleccionados.any { it.equals(estadoTurno, ignoreCase = true) }
                } else {
                    true
                }

                // 2. Filtro por Profesional Médico
                val cumpleProfesional = if (filtros.profesionalQuery.isNotBlank()) {
                    val query = filtros.profesionalQuery.trim()
                    turno.nombreMedico?.contains(query, ignoreCase = true) == true
                } else {
                    true
                }

                // 3. Filtro por Rango de Fechas
                val cumpleFechas = if (fechaDesdeDate != null || fechaHastaDate != null) {
                    val turnoDate = extraerFechaTurno(turno.fechaHora)
                    if (turnoDate != null) {
                        val cumpleDesde = fechaDesdeDate?.let { !turnoDate.before(it) } ?: true
                        val cumpleHasta = fechaHastaDate?.let { !turnoDate.after(it) } ?: true
                        cumpleDesde && cumpleHasta
                    } else {
                        true
                    }
                } else {
                    true
                }

                cumpleEstado && cumpleProfesional && cumpleFechas
            }
        }

        private fun extraerFechaTurno(fechaHoraStr: String?): java.util.Date? {
            if (fechaHoraStr.isNullOrBlank()) return null
            val formatos = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US),
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US),
                SimpleDateFormat("yyyy-MM-dd", Locale.US)
            )
            for (format in formatos) {
                try {
                    val parsed = format.parse(fechaHoraStr.trim())
                    if (parsed != null) {
                        // Normalizamos al inicio del día para comparación estricta de fecha
                        val cal = java.util.Calendar.getInstance()
                        cal.time = parsed
                        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        cal.set(java.util.Calendar.MINUTE, 0)
                        cal.set(java.util.Calendar.SECOND, 0)
                        cal.set(java.util.Calendar.MILLISECOND, 0)
                        return cal.time
                    }
                } catch (_: Exception) { }
            }
            return null
        }
    }
}
