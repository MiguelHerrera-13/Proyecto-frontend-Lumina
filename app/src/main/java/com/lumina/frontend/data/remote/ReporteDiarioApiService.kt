package com.lumina.frontend.data.remote

import com.lumina.frontend.data.remote.dto.ReporteDiarioRequestDto
import com.lumina.frontend.data.remote.dto.ReporteDiarioResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReporteDiarioApiService {

    @POST("api/reportes-diarios")
    suspend fun crearReporteDiario(
        @Body request: ReporteDiarioRequestDto
    ): Response<ReporteDiarioResponseDto>

    @GET("api/reportes-diarios/turno/{idTurno}")
    suspend fun obtenerReportesPorTurno(
        @Path("idTurno") idTurno: Long
    ): Response<List<ReporteDiarioResponseDto>>
}

