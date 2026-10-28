package com.lumina.frontend.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Clase utilitaria para exportar turnos médicos a la aplicación de calendario nativa de Android.
 *
 * Utiliza CalendarContract.Events.CONTENT_URI con un Intent implícito (ACTION_INSERT).
 *
 * Aspectos de diseño y permisos:
 * - NO requiere permisos sensibles en tiempo de ejecución (WRITE_CALENDAR / READ_CALENDAR)
 *   debido a que delega la inserción y confirmación del evento a la app de calendario del sistema.
 * - Maneja de forma segura la ausencia de aplicaciones de calendario mediante ActivityNotFoundException.
 * - Precarga automáticamente título, fechas de inicio y fin, médico asignado, motivo y ubicación.
 */
object CalendarExportHelper {

    /**
     * Lanza un Intent nativo para añadir el turno al calendario del dispositivo Android.
     *
     * @param context Contexto de Android para iniciar la actividad del Intent.
     * @param turno Objeto TurnoResponseDto con la información del turno a exportar.
     * @param onFeedback Callback opcional para notificar el resultado a la UI (ej. mediante Snackbar).
     * @return true si la actividad de calendario se inició exitosamente, false en caso contrario.
     */
    fun exportarTurnoAlCalendario(
        context: Context,
        turno: TurnoResponseDto,
        onFeedback: ((Boolean, String) -> Unit)? = null
    ): Boolean {
        val (startTimeMillis, endTimeMillis) = parsearRangoFechas(turno.fechaHora)

        val titulo = "Turno Médico: ${turno.nombreMedico ?: "Dr. Asignado"} - Lúmina"
        val descripcion = buildString {
            append("Turno médico agendado a través de Lúmina.\n\n")
            append("• Profesional: ${turno.nombreMedico ?: "Dr. Asignado"}\n")
            append("• Estado: ${turno.estado ?: "PROGRAMADO"}\n")
            if (!turno.motivo.isNullOrBlank()) {
                append("• Motivo: ${turno.motivo}\n")
            }
            append("• ID Turno: #${turno.id}")
        }
        val ubicacion = "Centro Médico Lúmina / Asistencia Médica"

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, titulo)
            putExtra(CalendarContract.Events.DESCRIPTION, descripcion)
            putExtra(CalendarContract.Events.EVENT_LOCATION, ubicacion)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMillis)
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(intent)
            onFeedback?.invoke(true, "Abriendo calendario con los datos precargados...")
            true
        } catch (e: ActivityNotFoundException) {
            val mensajeError = "No se encontró ninguna aplicación de calendario disponible en el dispositivo."
            Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
            onFeedback?.invoke(false, mensajeError)
            false
        } catch (e: Exception) {
            val mensajeError = "No se pudo exportar el turno al calendario: ${e.localizedMessage}"
            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
            onFeedback?.invoke(false, mensajeError)
            false
        }
    }

    /**
     * Parsea la fecha y hora del turno a milisegundos para CalendarContract.
     * Si no se puede parsear, utiliza una hora por defecto y duración estándar de 1 hora.
     */
    fun parsearRangoFechas(fechaHoraStr: String?): Pair<Long, Long> {
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US),
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )

        var parsedStartMillis: Long? = null
        if (!fechaHoraStr.isNullOrBlank()) {
            for (format in formats) {
                try {
                    val date = format.parse(fechaHoraStr.trim())
                    if (date != null) {
                        parsedStartMillis = date.time
                        break
                    }
                } catch (_: Exception) { }
            }
        }

        // Si la fecha es nula o inválida, se fija en 1 hora posterior a la actual
        val start = parsedStartMillis ?: (System.currentTimeMillis() + (60 * 60 * 1000L))
        // Duración estándar de 1 hora para el turno médico
        val end = start + (60 * 60 * 1000L)
        return Pair(start, end)
    }
}
