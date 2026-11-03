package com.lumina.frontend.data.remote.dto

data class PacienteResponseDto(
    val idPaciente: Long,
    val nombreCompleto: String,
    val fechaNacimiento: String,
    val diagnosticoFase: String?,
    val activo: Boolean,
    val idEmpleadoAsignado: Long? = null,
    val nombreEmpleadoAsignado: String? = null,
    val rolEmpleadoAsignado: String? = null,
    val idFamiliarAsignado: Long? = null,
    val nombreFamiliarAsignado: String? = null,
    val emailFamiliarAsignado: String? = null,
    val parentescoFamiliar: String? = null
)
