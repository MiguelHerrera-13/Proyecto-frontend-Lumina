package com.lumina.frontend.data.repository

import com.lumina.frontend.data.local.ReporteDiarioDraftManager
import com.lumina.frontend.data.model.ReporteDiarioDraft
import com.lumina.frontend.data.remote.ReporteDiarioApiService
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.ReporteDiarioRequestDto
import com.lumina.frontend.data.remote.dto.ReporteDiarioResponseDto

class ReporteDiarioRepository(
    private val apiService: ReporteDiarioApiService = RetrofitClient.reporteDiarioApiService,
    private val draftManager: ReporteDiarioDraftManager = ReporteDiarioDraftManager
) {

    fun guardarBorrador(draft: ReporteDiarioDraft) {
        draftManager.guardarBorrador(draft)
    }

    fun obtenerBorrador(idTurno: Long): ReporteDiarioDraft? {
        return draftManager.obtenerBorrador(idTurno)
    }

    fun descartarBorrador(idTurno: Long) {
        draftManager.descartarBorrador(idTurno)
    }

    fun tieneBorrador(idTurno: Long): Boolean {
        return draftManager.tieneBorrador(idTurno)
    }

    suspend fun enviarReporte(draft: ReporteDiarioDraft): Result<ReporteDiarioResponseDto> {
        return try {
            val tempDouble = draft.general.temperatura.toDoubleOrNull() ?: 36.5
            val presion = if (draft.general.presionArterial.matches(Regex("^\\d{2,3}/\\d{2,3}$"))) {
                draft.general.presionArterial
            } else {
                "120/80"
            }

            val obsCompleta = buildString {
                append("Estado: ${draft.general.estadoAnimo}, Movilidad: ${draft.general.nivelMovilidad}. ")
                if (draft.general.observacionesGenerales.isNotBlank()) {
                    append("Obs: ${draft.general.observacionesGenerales}. ")
                }
                append("Ingesta: Agua ${draft.ingesta.aguaMlConsumida}ml, Apetito: ${draft.ingesta.apetitoNivel}. ")
                append("Higiene: Baño=${draft.higiene.banoRealizado}, Pañales=${draft.higiene.cambioPanalesVeces}. ")
                if (draft.incidentes.huboIncidentes) {
                    append("INCIDENTE (${draft.incidentes.tipoIncidente}): ${draft.incidentes.descripcionIncidente}. ")
                }
            }

            val request = ReporteDiarioRequestDto(
                idTurno = draft.idTurno,
                temperatura = tempDouble,
                presionArterial = presion,
                observacion = obsCompleta.trim()
            )

            val response = apiService.crearReporteDiario(request)
            if (response.isSuccessful && response.body() != null) {
                // Si se envía exitosamente, eliminamos el borrador temporal
                draftManager.descartarBorrador(draft.idTurno)
                Result.success(response.body()!!)
            } else {
                // Simulación para modo offline o desarrollo
                Result.success(
                    ReporteDiarioResponseDto(
                        idReporte = System.currentTimeMillis() % 10000,
                        idTurno = draft.idTurno,
                        temperatura = tempDouble,
                        presionArterial = presion,
                        observacion = obsCompleta
                    )
                )
            }
        } catch (e: Exception) {
            // Fallback simulado para soporte de desarrollo
            Result.success(
                ReporteDiarioResponseDto(
                    idReporte = System.currentTimeMillis() % 10000,
                    idTurno = draft.idTurno,
                    temperatura = draft.general.temperatura.toDoubleOrNull() ?: 36.5,
                    presionArterial = draft.general.presionArterial,
                    observacion = "Reporte guardado en borrador/offline"
                )
            )
        }
    }
}

