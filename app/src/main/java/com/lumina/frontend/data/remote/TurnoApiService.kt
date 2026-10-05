package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TurnoApiService {
    @GET("/api/turnos/paciente/{id}")
    suspend fun getTurnosPaciente(@Path("id") pacienteId: Long): Response<List<TurnoResponseDto>>
}
