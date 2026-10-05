package com.lumina.frontend.data.repository

import com.lumina.frontend.data.remote.UsuarioApiService
import com.lumina.frontend.data.remote.dto.UsuarioRequestDto
import com.lumina.frontend.data.remote.dto.UsuarioResponseDto
import retrofit2.Response

class UsuarioRepository(private val apiService: UsuarioApiService) {
    suspend fun registrarUsuario(request: UsuarioRequestDto): Response<UsuarioResponseDto> {
        return apiService.registrarUsuario(request)
    }
}
