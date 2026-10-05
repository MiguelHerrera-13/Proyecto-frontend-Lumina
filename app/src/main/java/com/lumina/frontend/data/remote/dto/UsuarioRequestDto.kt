package com.lumina.frontend.data.remote.dto

data class UsuarioRequestDto(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String
)
