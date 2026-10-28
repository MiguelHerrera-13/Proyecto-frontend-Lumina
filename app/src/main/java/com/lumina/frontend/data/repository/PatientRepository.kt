package com.lumina.frontend.data.repository

import com.lumina.frontend.data.local.FakePatientDataSource
import com.lumina.frontend.data.remote.PacienteApiService
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto

class PatientRepository(
    private val apiService: PacienteApiService = RetrofitClient.pacienteApiService
) {

    suspend fun obtenerPacientes(): Result<List<PacienteResponseDto>> {
        return try {
            val response = apiService.obtenerPacientes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Fallback a datos simulados para soporte offline / desarrollo
                Result.success(FakePatientDataSource.getPatientsDto())
            }
        } catch (e: Exception) {
            // Fallback amigable ante corte de red / servidor no disponible
            Result.success(FakePatientDataSource.getPatientsDto())
        }
    }

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

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                fallbackPacienteFake(id)
            }
        } catch (e: Exception) {
            fallbackPacienteFake(id)
        }
    }

    private fun fallbackPacienteFake(id: Long): Result<PacienteResponseDto> {
        val fakeList = FakePatientDataSource.getPatientsDto()
        val paciente = fakeList.find { it.idPaciente == id } ?: fakeList.firstOrNull()
        return if (paciente != null) {
            Result.success(paciente)
        } else {
            Result.failure(Exception("Paciente no encontrado"))
        }
    }
}
