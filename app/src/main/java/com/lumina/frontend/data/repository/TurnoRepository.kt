package com.lumina.frontend.data.repository

import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.TurnoResponseDto

class TurnoRepository {
    private val api = RetrofitClient.turnoApiService

    suspend fun obtenerTurnos(pacienteId: Long): Result<List<TurnoResponseDto>> {
        return try {
            val response = api.getTurnosPaciente(pacienteId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error en la API: "))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
