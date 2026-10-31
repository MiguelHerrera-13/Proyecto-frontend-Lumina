package com.lumina.frontend.data.repository

import com.lumina.frontend.data.local.FakePatientDataSource
import com.lumina.frontend.data.local.FakePatientDetailDataSource
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.TurnoApiService
import com.lumina.frontend.data.remote.dto.HistorialResponseDto
import com.lumina.frontend.data.remote.dto.MedicamentoResponseDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
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
        return Result.success(FakePatientDetailDataSource.getMedicamentosFake(pacienteId))
    }

    suspend fun getHistorialPaciente(pacienteId: Long): Result<List<HistorialResponseDto>> {
        return Result.success(FakePatientDetailDataSource.getHistorialFake(pacienteId))
    }

    suspend fun actualizarPaciente(
        id: Long,
        nombre: String,
        fechaNacimiento: String,
        diagnostico: String
    ): Result<PacienteResponseDto> {
        val updated = FakePatientDataSource.updatePatient(id, nombre, fechaNacimiento, diagnostico)
        return if (updated != null) {
            Result.success(updated)
        } else {
            Result.failure(Exception("No se pudo actualizar el paciente"))
        }
    }
}
