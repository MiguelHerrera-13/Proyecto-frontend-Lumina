package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.UsuarioRequestDto
import com.lumina.frontend.data.remote.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioApiService {
    @POST("api/usuarios")
    suspend fun registrarUsuario(@Body request: UsuarioRequestDto): Response<UsuarioResponseDto>
}
