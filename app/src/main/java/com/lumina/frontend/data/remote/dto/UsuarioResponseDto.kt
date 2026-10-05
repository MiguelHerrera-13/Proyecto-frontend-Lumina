package com.lumina.frontend.data.remote.dto

data class UsuarioResponseDto(
    val idUsuario: Long?,
    val nombre: String,
    val email: String,
    val rol: String
)
