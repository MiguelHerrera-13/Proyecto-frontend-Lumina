package com.lumina.frontend.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TurnoResponseDto(
    @SerializedName(value = "id", alternate = ["idTurno"])
    val id: Long = 0,
    @SerializedName(value = "fechaHora", alternate = ["fechaHoraInicio"])
    val fechaHora: String = "",
    val motivo: String? = "Consulta médica",
    val estado: String? = "CONFIRMADO",
    val nombreMedico: String? = "Dr. Asignado",
    val nombrePaciente: String? = "Roberto Gómez",
    val habitacion: String? = "Habitación 204",
    val horaInicio: String? = null,
    val horaFin: String? = null
)
