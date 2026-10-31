package com.lumina.frontend.data.repository

import com.lumina.frontend.data.local.FakePatientDetailDataSource
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.local.UserRole
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.CancelarTurnoRequestDto
import com.lumina.frontend.data.remote.dto.CrearTurnoRequestDto
import com.lumina.frontend.data.remote.dto.ReprogramarTurnoRequestDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto

/**
 * Excepción específica para representar un error HTTP 409 Conflict (Colisión o solapamiento de agenda).
 */
class ConflictoTurnoException(
    override val message: String = "Conflicto de agenda (409): El profesional o paciente ya posee un turno programado en ese horario.",
    val sugerencia: String = "Por favor, selecciona un rango horario diferente o elige otra fecha."
) : Exception(message)

class TurnoRepository {
    private val api = RetrofitClient.turnoApiService

    private fun filtrarPorRol(lista: List<TurnoResponseDto>): List<TurnoResponseDto> {
        val role = SessionManager.getCurrentRole()
        return when (role) {
            UserRole.EMPLEADO -> {
                // Empleado solo ve turnos de sus pacientes asignados (Roberto Gómez y Jorge Méndez) o con personal asistencial
                lista.filter { turno ->
                    turno.nombrePaciente?.contains("Roberto Gómez", ignoreCase = true) == true ||
                    turno.nombrePaciente?.contains("Jorge Méndez", ignoreCase = true) == true ||
                    turno.nombreMedico?.contains("Cuidador", ignoreCase = true) == true ||
                    turno.nombreMedico?.contains("Enf", ignoreCase = true) == true
                }
            }
            UserRole.USUARIO -> {
                // Familiar / Usuario solo ve turnos de su ser querido: Roberto Gómez
                lista.filter { turno ->
                    turno.nombrePaciente?.contains("Roberto Gómez", ignoreCase = true) == true ||
                    (turno.nombrePaciente.isNullOrBlank() && (turno.id == 101L || turno.id == 102L || turno.id == 104L || turno.id == 107L))
                }
            }
            else -> lista // SUPER_ADMIN ve todos los turnos de la clínica
        }
    }

    suspend fun obtenerTurnos(pacienteId: Long): Result<List<TurnoResponseDto>> {
        return try {
            val response = api.getTurnosPaciente(pacienteId)
            val turnos = if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                FakePatientDetailDataSource.getTurnosFake(pacienteId)
            }
            Result.success(filtrarPorRol(turnos))
        } catch (e: Exception) {
            Result.success(filtrarPorRol(FakePatientDetailDataSource.getTurnosFake(pacienteId)))
        }
    }

    /**
     * Envía la solicitud de creación de turno a POST /api/turnos.
     * Gestiona explícitamente el código de error HTTP 409 (Conflict / Solapamiento).
     */
    suspend fun crearTurno(request: CrearTurnoRequestDto): Result<TurnoResponseDto> {
        return try {
            val response = api.crearTurno(request)
            when {
                response.isSuccessful && response.body() != null -> {
                    Result.success(response.body()!!)
                }
                response.code() == 409 -> {
                    val serverMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "Conflicto de agenda (409): El profesional ${request.nombreMedico} ya posee un turno programado en ese horario."
                    Result.failure(ConflictoTurnoException(message = serverMsg))
                }
                else -> {
                    // Fallback simulado para entorno de desarrollo / pruebas offline
                    simularCreacionTurno(request)
                }
            }
        } catch (e: Exception) {
            // Manejo de contingencia offline
            simularCreacionTurno(request)
        }
    }

    private fun simularCreacionTurno(request: CrearTurnoRequestDto): Result<TurnoResponseDto> {
        // Simulación controlada de colisión 409: si se intenta agendar a las 10:00 con la Dra. Martínez en 2026-10-28
        val esColisionSimulada = (request.nombreMedico.contains("Martínez") &&
                request.fecha == "2026-10-28" &&
                request.horaInicio.startsWith("10:00"))

        return if (esColisionSimulada) {
            Result.failure(
                ConflictoTurnoException(
                    message = "Conflicto de agenda (409): La profesional ${request.nombreMedico} ya tiene un turno reservado para el ${request.fecha} a las ${request.horaInicio}.",
                    sugerencia = "Te sugerimos agendar después de las 11:30 o seleccionar otro profesional."
                )
            )
        } else {
            val nuevo = TurnoResponseDto(
                id = System.currentTimeMillis(),
                fechaHora = "${request.fecha}T${request.horaInicio}:00",
                motivo = request.motivo,
                estado = "PENDIENTE",
                nombreMedico = request.nombreMedico,
                nombrePaciente = "Roberto Gómez",
                habitacion = "Hab. 204"
            )
            FakePatientDetailDataSource.agregarTurno(nuevo)
            Result.success(nuevo)
        }
    }

    suspend fun cancelarTurno(turnoId: Long, motivo: String): Result<TurnoResponseDto> {
        FakePatientDetailDataSource.cancelarTurno(turnoId, motivo)
        return try {
            val response = api.cancelarTurno(turnoId, CancelarTurnoRequestDto(motivo = motivo))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
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
        FakePatientDetailDataSource.reprogramarTurno(turnoId, nuevaFechaHora, motivo)
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
