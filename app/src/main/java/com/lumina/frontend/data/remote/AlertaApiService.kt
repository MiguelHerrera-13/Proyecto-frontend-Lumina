package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.AlertaResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AlertaApiService {

    @GET("/api/alertas/medico/{id}")
    suspend fun getAlertasMedico(@Path("id") medicoId: Long): Response<List<AlertaResponseDto>>

}
