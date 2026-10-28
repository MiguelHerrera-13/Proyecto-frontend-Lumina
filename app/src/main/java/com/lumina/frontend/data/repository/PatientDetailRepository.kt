package com.lumina.frontend.data.repository

import com.lumina.frontend.data.local.FakePatientDetailDataSource
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.TurnoApiService
import com.lumina.frontend.data.remote.dto.HistorialResponseDto
import com.lumina.frontend.data.remote.dto.MedicamentoResponseDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto

class PatientDetailRepository(
    private val turnoApiService: TurnoApiService = RetrofitClient.turnoApiService
) {

    suspend fun getTurnosPaciente(pacienteId: Long): Result<List<TurnoResponseDto>> {
        return try {
            val response = turnoApiService.getTurnosPaciente(pacienteId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(FakePatientDetailDataSource.getTurnosFake(pacienteId))
            }
        } catch (e: Exception) {
            Result.success(FakePatientDetailDataSource.getTurnosFake(pacienteId))
        }
    }

    suspend fun getMedicamentosPaciente(pacienteId: Long): Result<List<MedicamentoResponseDto>> {
        // Por ahora devuelve desde FakePatientDetailDataSource con fallback reactivo
        return Result.success(FakePatientDetailDataSource.getMedicamentosFake(pacienteId))
    }

    suspend fun getHistorialPaciente(pacienteId: Long): Result<List<HistorialResponseDto>> {
        // Por ahora devuelve desde FakePatientDetailDataSource con fallback reactivo
        return Result.success(FakePatientDetailDataSource.getHistorialFake(pacienteId))
    }
}

