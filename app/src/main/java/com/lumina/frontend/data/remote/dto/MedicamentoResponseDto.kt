package com.lumina.frontend.data.remote.dto

data class MedicamentoResponseDto(
    val idMedicamento: Long,
    val nombreMedicamento: String,
    val dosis: String?,
    val frecuencia: String?,
    val via: String? = null,           // oral, inyectable, etc.
    val activo: Boolean = true
)
