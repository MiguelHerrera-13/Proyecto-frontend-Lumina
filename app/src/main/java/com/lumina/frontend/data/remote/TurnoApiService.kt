package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.CancelarTurnoRequestDto
import com.lumina.frontend.data.remote.dto.ReprogramarTurnoRequestDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TurnoApiService {
    @GET("/api/turnos/cuidador/{id}")
    suspend fun getTurnosPaciente(@Path("id") pacienteId: Long): Response<List<TurnoResponseDto>>

    @POST("/api/turnos/{id}/cancelar")
    suspend fun cancelarTurno(
        @Path("id") turnoId: Long,
        @Body request: CancelarTurnoRequestDto
    ): Response<TurnoResponseDto>

    @POST("/api/turnos/{id}/reprogramar")
    suspend fun reprogramarTurno(
        @Path("id") turnoId: Long,
        @Body request: ReprogramarTurnoRequestDto
    ): Response<TurnoResponseDto>
}
