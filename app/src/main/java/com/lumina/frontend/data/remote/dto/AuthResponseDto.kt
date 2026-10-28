package com.lumina.frontend.data.remote.dto

data class AuthResponseDto(
    val token: String,
    val tipo: String = "Bearer",
    val email: String,
    val rol: String,
    val refreshToken: String? = null
)
