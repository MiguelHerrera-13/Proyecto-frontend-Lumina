package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.AuthResponseDto
import com.lumina.frontend.data.remote.dto.LoginRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>
}
