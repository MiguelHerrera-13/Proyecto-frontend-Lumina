package com.lumina.frontend.data.remote.dto

/**
 * DTO para la solicitud de creación o reserva de un nuevo turno médico.
 *
 * Cumple con los requerimientos de la Issue Frontend #21 (Refs #29):
 * - Selección de paciente y profesional asignado.
 * - Fecha del turno ("yyyy-MM-dd").
 * - Rango horario: hora de inicio y fin ("HH:mm").
 * - Motivo o justificación de la consulta.
 */
data class CrearTurnoRequestDto(
    val pacienteId: Long,
    val nombreMedico: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val motivo: String,
    val fechaHora: String = "$fecha $horaInicio"
)
