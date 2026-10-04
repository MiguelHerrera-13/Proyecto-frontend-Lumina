package com.lumina.frontend.data.remote.dto

data class PacienteResponseDto(
    val idPaciente: Long,
    val nombreCompleto: String,
    val fechaNacimiento: String,
    val diagnosticoFase: String?,
    val activo: Boolean
)