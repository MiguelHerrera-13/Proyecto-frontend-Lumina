package com.lumina.frontend.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AlertaResponseDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName(value = "idReporte", alternate = ["reporteId"])
    val idReporte: Long? = null,

    @SerializedName(value = "idMedico", alternate = ["medicoId"])
    val idMedico: Long? = null,

    @SerializedName(value = "nombrePaciente", alternate = ["pacienteNombre", "paciente"])
    val nombrePaciente: String? = "Paciente Asignado",

    @SerializedName(value = "mensajeAlerta", alternate = ["mensaje", "descripcion"])
    val mensajeAlerta: String,

    @SerializedName(value = "urgente", alternate = ["esUrgente", "critica", "atencionUrgente"])
    val urgente: Boolean = false,

    @SerializedName(value = "leida", alternate = ["esLeida"])
    val leida: Boolean = false,

    @SerializedName(value = "fechaHora", alternate = ["fecha", "timestamp"])
    val fechaHora: String? = null
)
