package com.lumina.frontend.data.remote.dto

data class LoginResponseDto(
    val token: String? = null,
    val tipo: String? = "Bearer",
    val email: String? = null,
    val rol: String? = null,
    val refreshToken: String? = null,
    val nombre: String? = null
)
