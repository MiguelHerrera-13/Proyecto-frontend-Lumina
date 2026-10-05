package com.lumina.frontend.data.remote.dto

data class TurnoResponseDto(
    val id: Long,
    val fechaHora: String,
    val motivo: String?,
    val estado: String?,
    val nombreMedico: String? = "Dr. Asignado" // Si el backend no lo envía aún, ponemos un placeholder
)
