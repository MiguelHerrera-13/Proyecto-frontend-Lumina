package com.lumina.frontend.data.remote.dto

data class PacienteRequestDto(
    val nombreCompleto: String,
    val fechaNacimiento: String,
    val diagnosticoFase: String?
)