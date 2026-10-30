package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.ActualizarUsuarioRequestDto
import com.lumina.frontend.data.remote.dto.UsuarioPerfilDto
import com.lumina.frontend.data.remote.dto.UsuarioRequestDto
import com.lumina.frontend.data.remote.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApiService {
    @POST("api/usuarios")
    suspend fun registrarUsuario(@Body request: UsuarioRequestDto): Response<UsuarioResponseDto>

    @GET("api/auth/me")
    suspend fun obtenerPerfil(): Response<UsuarioPerfilDto>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body request: ActualizarUsuarioRequestDto
    ): Response<UsuarioPerfilDto>
}
