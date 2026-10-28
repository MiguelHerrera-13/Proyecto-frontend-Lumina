package com.lumina.frontend.data.remote.dto

data class TurnoResponseDto(
    val id: Long,
    val fechaHora: String,
    val motivo: String?,
    val estado: String?,
    val nombreMedico: String? = "Dr. Asignado",
    val nombrePaciente: String? = "María Elena González",
    val habitacion: String? = "Habitación 204",
    val horaInicio: String? = null,
    val horaFin: String? = null
)
