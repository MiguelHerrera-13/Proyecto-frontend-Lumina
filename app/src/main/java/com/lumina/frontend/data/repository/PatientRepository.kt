package com.lumina.frontend.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lumina.frontend.data.local.FakePatientDataSource
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.local.UserRole
import com.lumina.frontend.data.paging.PatientPagingSource
import com.lumina.frontend.data.remote.PacienteApiService
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.PacienteRequestDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import kotlinx.coroutines.flow.Flow

class PatientRepository(
    private val apiService: PacienteApiService = RetrofitClient.pacienteApiService
) {

    fun getPatientsPaged(pageSize: Int = PatientPagingSource.DEFAULT_PAGE_SIZE): Flow<PagingData<PacienteResponseDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize
            ),
            pagingSourceFactory = { PatientPagingSource(apiService) }
        ).flow
    }

    private fun filtrarPorRol(lista: List<PacienteResponseDto>): List<PacienteResponseDto> {
        val role = SessionManager.getCurrentRole()
        return when (role) {
            UserRole.EMPLEADO -> lista.filter { it.idPaciente == 1L || it.idPaciente == 4L }
            UserRole.USUARIO -> lista.filter { it.idPaciente == 1L }
            else -> lista
        }
    }

    suspend fun obtenerPacientes(): Result<List<PacienteResponseDto>> {
        return try {
            val response = apiService.obtenerPacientes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(filtrarPorRol(response.body()!!))
            } else {
                Result.success(filtrarPorRol(FakePatientDataSource.getPatientsDto()))
            }
        } catch (e: Exception) {
            Result.success(filtrarPorRol(FakePatientDataSource.getPatientsDto()))
        }
    }

    suspend fun crearPaciente(
        paciente: PacienteRequestDto
    ): Result<PacienteResponseDto> {
        return try {
            val response = apiService.crearPaciente(paciente)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val localPatient = FakePatientDataSource.addPatient(
                    nombreCompleto = paciente.nombreCompleto,
                    fechaNacimiento = paciente.fechaNacimiento,
                    diagnosticoFase = paciente.diagnosticoFase ?: "Control clínico general"
                )
                Result.success(localPatient)
            }
        } catch (e: Exception) {
            val localPatient = FakePatientDataSource.addPatient(
                nombreCompleto = paciente.nombreCompleto,
                fechaNacimiento = paciente.fechaNacimiento,
                diagnosticoFase = paciente.diagnosticoFase ?: "Control clínico general"
            )
            Result.success(localPatient)
        }
    }

    suspend fun actualizarPaciente(
        id: Long,
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String
    ): Result<PacienteResponseDto> {
        val updated = FakePatientDataSource.updatePatient(id, nombreCompleto, fechaNacimiento, diagnosticoFase)
        return if (updated != null) {
            Result.success(updated)
        } else {
            Result.failure(Exception("No se encontró el paciente para actualizar"))
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
