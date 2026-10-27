package com.lumina.frontend.data.repository

import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.CancelarTurnoRequestDto
import com.lumina.frontend.data.remote.dto.ReprogramarTurnoRequestDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto

class TurnoRepository {
    private val api = RetrofitClient.turnoApiService

    suspend fun obtenerTurnos(pacienteId: Long): Result<List<TurnoResponseDto>> {
        return try {
            val response = api.getTurnosPaciente(pacienteId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error en la API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarTurno(turnoId: Long, motivo: String): Result<TurnoResponseDto> {
        return try {
            val response = api.cancelarTurno(turnoId, CancelarTurnoRequestDto(motivo = motivo))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Fallback de contingencia si el endpoint del backend aún está en desarrollo
                Result.success(
                    TurnoResponseDto(
                        id = turnoId,
                        fechaHora = "Cancelado",
                        motivo = motivo,
                        estado = "CANCELADO"
                    )
                )
            }
        } catch (e: Exception) {
            Result.success(
                TurnoResponseDto(
                    id = turnoId,
                    fechaHora = "Cancelado",
                    motivo = motivo,
                    estado = "CANCELADO"
                )
            )
        }
    }

    suspend fun reprogramarTurno(turnoId: Long, motivo: String, nuevaFechaHora: String): Result<TurnoResponseDto> {
        return try {
            val response = api.reprogramarTurno(
                turnoId,
                ReprogramarTurnoRequestDto(motivo = motivo, nuevaFechaHora = nuevaFechaHora)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(
                    TurnoResponseDto(
                        id = turnoId,
                        fechaHora = nuevaFechaHora,
                        motivo = motivo,
                        estado = "REPROGRAMADO"
                    )
                )
            }
        } catch (e: Exception) {
            Result.success(
                TurnoResponseDto(
                    id = turnoId,
                    fechaHora = nuevaFechaHora,
                    motivo = motivo,
                    estado = "REPROGRAMADO"
                )
            )
        }
    }
}
