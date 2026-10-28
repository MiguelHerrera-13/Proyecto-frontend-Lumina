package com.lumina.frontend.data.local

import com.lumina.frontend.data.remote.dto.HistorialResponseDto
import com.lumina.frontend.data.remote.dto.MedicamentoResponseDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto

object FakePatientDetailDataSource {

    fun getTurnosFake(pacienteId: Long): List<TurnoResponseDto> = listOf(
        TurnoResponseDto(
            id = 101L,
            fechaHora = "2026-10-28T10:00:00",
            motivo = "Control mensual",
            estado = "CONFIRMADO",
            nombreMedico = "Dra. Martínez"
        ),
        TurnoResponseDto(
            id = 102L,
            fechaHora = "2026-11-05T14:30:00",
            motivo = "Seguimiento neurológico",
            estado = "PENDIENTE",
            nombreMedico = "Dr. Sánchez"
        ),
        TurnoResponseDto(
            id = 103L,
            fechaHora = "2026-09-15T09:00:00",
            motivo = "Revisión general",
            estado = "COMPLETADO",
            nombreMedico = "Dra. Martínez"
        )
    )

    fun getMedicamentosFake(pacienteId: Long): List<MedicamentoResponseDto> = listOf(
        MedicamentoResponseDto(
            idMedicamento = 201L,
            nombreMedicamento = "Donepezilo",
            dosis = "10mg",
            frecuencia = "Una vez al día",
            via = "Oral",
            activo = true
        ),
        MedicamentoResponseDto(
            idMedicamento = 202L,
            nombreMedicamento = "Memantina",
            dosis = "20mg",
            frecuencia = "Dos veces al día",
            via = "Oral",
            activo = true
        ),
        MedicamentoResponseDto(
            idMedicamento = 203L,
            nombreMedicamento = "Lorazepam",
            dosis = "1mg",
            frecuencia = "Según necesidad",
            via = "Oral",
            activo = false
        )
    )

    fun getHistorialFake(pacienteId: Long): List<HistorialResponseDto> = listOf(
        HistorialResponseDto(
            idRegistro = 301L,
            fecha = "2026-10-01",
            descripcion = "Paciente estable. Sin cambios significativos en el estado cognitivo.",
            tipo = "CONSULTA",
            profesional = "Dra. Martínez"
        ),
        HistorialResponseDto(
            idRegistro = 302L,
            fecha = "2026-08-20",
            descripcion = "Episodio de desorientación nocturna. Se ajusta medicación.",
            tipo = "OBSERVACION",
            profesional = "Enf. González"
        ),
        HistorialResponseDto(
            idRegistro = 303L,
            fecha = "2026-06-15",
            descripcion = "Internación de 3 días por cuadro febril. Alta con antibiótico oral.",
            tipo = "INTERNACION",
            profesional = "Dr. Sánchez"
        )
    )
}
