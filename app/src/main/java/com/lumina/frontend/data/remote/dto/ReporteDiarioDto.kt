package com.lumina.frontend.data.remote.dto

data class ReporteDiarioRequestDto(
    val idTurno: Long,
    val temperatura: Double,
    val presionArterial: String,
    val observacion: String
)

data class ReporteDiarioResponseDto(
    val idReporte: Long,
    val idTurno: Long,
    val temperatura: Double,
    val presionArterial: String,
    val observacion: String,
    val fechaCreacion: String? = null
)

