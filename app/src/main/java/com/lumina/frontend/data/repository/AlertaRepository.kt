package com.lumina.frontend.data.repository

import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.AlertaResponseDto

class AlertaRepository {
    private val api = RetrofitClient.alertaApiService

    suspend fun obtenerAlertasMedico(medicoId: Long): Result<List<AlertaResponseDto>> {
        return try {
            val response = api.getAlertasMedico(medicoId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error en el servidor: código ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
