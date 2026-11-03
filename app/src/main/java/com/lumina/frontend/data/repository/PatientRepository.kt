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
        val session = SessionManager.currentSession.value
        val role = session?.rol
        val sessionEmail = session?.email?.lowercase() ?: ""
        val sessionNombre = session?.nombre?.lowercase() ?: ""

        return when (role) {
            UserRole.EMPLEADO -> {
                val asignados = lista.filter { p ->
                    val empName = p.nombreEmpleadoAsignado?.lowercase() ?: ""
                    empName.isNotBlank() && (empName.contains(sessionNombre) || sessionNombre.contains(empName))
                }
                if (asignados.isNotEmpty()) asignados else lista
            }
            UserRole.USUARIO -> {
                val familiares = lista.filter { p ->
                    val famEmail = p.emailFamiliarAsignado?.lowercase() ?: ""
                    val famNombre = p.nombreFamiliarAsignado?.lowercase() ?: ""
                    (famEmail.isNotBlank() && famEmail == sessionEmail) ||
                    (famNombre.isNotBlank() && (sessionNombre.contains(famNombre) || famNombre.contains(sessionNombre)))
                }
                if (familiares.isNotEmpty()) familiares else lista
            }
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
                val guardado = response.body()!!
                FakePatientDataSource.addPatient(
                    nombreCompleto = guardado.nombreCompleto,
                    fechaNacimiento = guardado.fechaNacimiento,
                    diagnosticoFase = guardado.diagnosticoFase ?: "Control clínico general",
                    idEmpleadoAsignado = guardado.idEmpleadoAsignado,
                    nombreEmpleadoAsignado = guardado.nombreEmpleadoAsignado,
                    rolEmpleadoAsignado = guardado.rolEmpleadoAsignado,
                    idFamiliarAsignado = guardado.idFamiliarAsignado,
                    nombreFamiliarAsignado = guardado.nombreFamiliarAsignado,
                    emailFamiliarAsignado = guardado.emailFamiliarAsignado,
                    parentescoFamiliar = guardado.parentescoFamiliar
                )
                Result.success(guardado)
            } else {
                val localPatient = FakePatientDataSource.addPatient(
                    nombreCompleto = paciente.nombreCompleto,
                    fechaNacimiento = paciente.fechaNacimiento,
                    diagnosticoFase = paciente.diagnosticoFase ?: "Control clínico general",
                    idEmpleadoAsignado = paciente.idEmpleadoAsignado,
                    nombreEmpleadoAsignado = paciente.nombreEmpleadoAsignado,
                    rolEmpleadoAsignado = paciente.rolEmpleadoAsignado,
                    idFamiliarAsignado = paciente.idFamiliarAsignado,
                    nombreFamiliarAsignado = paciente.nombreFamiliarAsignado,
                    emailFamiliarAsignado = paciente.emailFamiliarAsignado,
                    parentescoFamiliar = paciente.parentescoFamiliar
                )
                Result.success(localPatient)
            }
        } catch (e: Exception) {
            val localPatient = FakePatientDataSource.addPatient(
                nombreCompleto = paciente.nombreCompleto,
                fechaNacimiento = paciente.fechaNacimiento,
                diagnosticoFase = paciente.diagnosticoFase ?: "Control clínico general",
                idEmpleadoAsignado = paciente.idEmpleadoAsignado,
                nombreEmpleadoAsignado = paciente.nombreEmpleadoAsignado,
                rolEmpleadoAsignado = paciente.rolEmpleadoAsignado,
                idFamiliarAsignado = paciente.idFamiliarAsignado,
                nombreFamiliarAsignado = paciente.nombreFamiliarAsignado,
                emailFamiliarAsignado = paciente.emailFamiliarAsignado,
                parentescoFamiliar = paciente.parentescoFamiliar
            )
            Result.success(localPatient)
        }
    }

    suspend fun asignarFamiliar(
        id: Long,
        nombreFamiliar: String,
        emailFamiliar: String,
        parentesco: String
    ): Result<PacienteResponseDto> {
        return try {
            val body = mapOf(
                "nombreFamiliar" to nombreFamiliar,
                "emailFamiliar" to emailFamiliar,
                "parentesco" to parentesco
            )
            val response = apiService.asignarFamiliar(id, body)
            if (response.isSuccessful && response.body() != null) {
                val guardado = response.body()!!
                Result.success(guardado)
            } else {
                val local = FakePatientDataSource.updatePatient(
                    id = id,
                    nombreCompleto = "",
                    fechaNacimiento = "",
                    diagnosticoFase = "",
                    nombreFamiliarAsignado = nombreFamiliar,
                    emailFamiliarAsignado = emailFamiliar,
                    parentescoFamiliar = parentesco
                )
                if (local != null) Result.success(local) else Result.failure(Exception("Error al asignar familiar"))
            }
        } catch (e: Exception) {
            val local = FakePatientDataSource.updatePatient(
                id = id,
                nombreCompleto = "",
                fechaNacimiento = "",
                diagnosticoFase = "",
                nombreFamiliarAsignado = nombreFamiliar,
                emailFamiliarAsignado = emailFamiliar,
                parentescoFamiliar = parentesco
            )
            if (local != null) Result.success(local) else Result.failure(e)
        }
    }

    suspend fun actualizarPaciente(
        id: Long,
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String
    ): Result<PacienteResponseDto> {
        return try {
            val response = apiService.actualizarPaciente(
                id,
                PacienteRequestDto(
                    nombreCompleto = nombreCompleto,
                    fechaNacimiento = fechaNacimiento,
                    diagnosticoFase = diagnosticoFase
                )
            )
            FakePatientDataSource.updatePatient(id, nombreCompleto, fechaNacimiento, diagnosticoFase)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val updated = FakePatientDataSource.updatePatient(id, nombreCompleto, fechaNacimiento, diagnosticoFase)
                if (updated != null) Result.success(updated) else Result.failure(Exception("No se encontró el paciente para actualizar"))
            }
        } catch (e: Exception) {
            val updated = FakePatientDataSource.updatePatient(id, nombreCompleto, fechaNacimiento, diagnosticoFase)
            if (updated != null) {
                Result.success(updated)
            } else {
                Result.failure(Exception("No se encontró el paciente para actualizar"))
            }
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
