package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.AuthResponseDto
import com.lumina.frontend.data.remote.dto.LoginRequestDto
import com.lumina.frontend.data.remote.dto.RefreshTokenRequestDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<AuthResponseDto>

    // Versión síncrona para ser invocada dentro de TokenAuthenticator (OkHttp Authenticator corre en background)
    @POST("api/auth/refresh")
    fun refreshTokenSync(@Body request: RefreshTokenRequestDto): Call<AuthResponseDto>
}
