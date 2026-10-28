package com.lumina.frontend.data.remote.dto

data class HistorialResponseDto(
    val idRegistro: Long,
    val fecha: String,
    val descripcion: String,
    val tipo: String,          // "CONSULTA", "INTERNACION", "OBSERVACION", etc.
    val profesional: String? = null
)
