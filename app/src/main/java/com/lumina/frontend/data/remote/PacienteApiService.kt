package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PacienteApiService {

    @POST("api/pacientes")
    suspend fun crearPaciente(
        @Body paciente: PacienteRequestDto
    ): Response<PacienteResponseDto>

    @GET("api/pacientes/{id}")
    suspend fun obtenerPacientePorId(
        @Path("id") id: Long
    ): Response<PacienteResponseDto>

    @GET("api/pacientes")
    suspend fun obtenerPacientes(): Response<List<PacienteResponseDto>>

    @GET("api/pacientes")
    suspend fun obtenerPacientesPaginados(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<PacienteResponseDto>>
}
