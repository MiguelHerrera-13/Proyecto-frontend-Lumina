package com.lumina.frontend.data.remote.dto

data class ControlIngestaRequestDto(
    val tipoComida: String,
    val descripcionComida: String? = null,
    val porcentajeConsumido: Int,
    val aguaMl: Int,
    val observaciones: String? = null
)

