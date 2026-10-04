package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PacienteApiService {

    @POST("api/pacientes")
    suspend fun crearPaciente(
        @Body paciente: PacienteRequestDto
    ): Response<PacienteResponseDto>

    @GET("api/pacientes/{id}")
    suspend fun obtenerPacientePorId(
        @Path("id") id: Long
    ): Response<PacienteResponseDto>
}