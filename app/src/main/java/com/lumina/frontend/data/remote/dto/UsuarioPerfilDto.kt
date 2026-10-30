package com.lumina.frontend.data.remote.dto

data class UsuarioPerfilDto(
    val idUsuario: Long,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val rol: String
)
