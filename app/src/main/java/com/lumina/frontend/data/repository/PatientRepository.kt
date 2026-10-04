package com.lumina.frontend.data.repository

import com.lumina.frontend.data.remote.PacienteApiService
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto

class PatientRepository(
    private val apiService: PacienteApiService = RetrofitClient.pacienteApiService
) {

    suspend fun crearPaciente(
        paciente: PacienteRequestDto
    ): Result<PacienteResponseDto> {
        return try {
            val response = apiService.crearPaciente(paciente)

            if (response.isSuccessful) {
                val pacienteCreado = response.body()

                if (pacienteCreado != null) {
                    Result.success(pacienteCreado)
                } else {
                    Result.failure(
                        Exception("El servidor respondió sin datos")
                    )
                }
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPacientePorId(
        id: Long
    ): Result<PacienteResponseDto> {
        return try {
            val response = apiService.obtenerPacientePorId(id)

            if (response.isSuccessful) {
                val paciente = response.body()

                if (paciente != null) {
                    Result.success(paciente)
                } else {
                    Result.failure(
                        Exception("El servidor respondió sin datos")
                    )
                }
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}