package com.lumina.frontend.data.local

import com.lumina.frontend.data.remote.dto.HistorialResponseDto
import com.lumina.frontend.data.remote.dto.MedicamentoResponseDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import java.time.LocalDate

object FakePatientDetailDataSource {

    private fun inicializarTurnos(): MutableList<TurnoResponseDto> {
        val hoy = LocalDate.now()
        val manana = hoy.plusDays(1)
        val pasadoManana = hoy.plusDays(2)
        val dia3 = hoy.plusDays(3)

        return mutableListOf(
            TurnoResponseDto(
                id = 101L,
                fechaHora = "${hoy}T09:00:00",
                motivo = "Control médico matutino y medicación",
                estado = "CONFIRMADO",
                nombreMedico = "Dra. Martínez",
                nombrePaciente = "Roberto Gómez",
                habitacion = "Hab. 204",
                horaInicio = "09:00",
                horaFin = "10:00"
            ),
            TurnoResponseDto(
                id = 102L,
                fechaHora = "${hoy}T10:30:00",
                motivo = "Monitoreo clínico, asistencia e hidratación",
                estado = "EN_CURSO",
                nombreMedico = "Lic. Camila Salomón",
                nombrePaciente = "Roberto Gómez",
                habitacion = "Hab. 204",
                horaInicio = "10:30",
                horaFin = "12:00"
            ),
            TurnoResponseDto(
                id = 103L,
                fechaHora = "${hoy}T14:00:00",
                motivo = "Kinesiología motora y estimulación física",
                estado = "PROGRAMADO",
                nombreMedico = "Lic. Camila Salomón",
                nombrePaciente = "Jorge Méndez",
                habitacion = "Hab. 108",
                horaInicio = "14:00",
                horaFin = "15:30"
            ),
            TurnoResponseDto(
                id = 104L,
                fechaHora = "${hoy}T16:30:00",
                motivo = "Acompañamiento en merienda y control de marcha",
                estado = "PROGRAMADO",
                nombreMedico = "Enf. Cuidador Principal",
                nombrePaciente = "Roberto Gómez",
                habitacion = "Hab. 204",
                horaInicio = "16:30",
                horaFin = "17:30"
            ),
            TurnoResponseDto(
                id = 105L,
                fechaHora = "${manana}T09:30:00",
                motivo = "Evaluación médica geriátrica",
                estado = "PROGRAMADO",
                nombreMedico = "Dr. Alejandro Gómez",
                nombrePaciente = "Roberto Gómez",
                habitacion = "Hab. 204",
                horaInicio = "09:30",
                horaFin = "10:30"
            ),
            TurnoResponseDto(
                id = 106L,
                fechaHora = "${manana}T11:30:00",
                motivo = "Terapia ocupacional y rehabilitación motora",
                estado = "PROGRAMADO",
                nombreMedico = "Lic. Camila Salomón",
                nombrePaciente = "Jorge Méndez",
                habitacion = "Hab. 108",
                horaInicio = "11:30",
                horaFin = "13:00"
            ),
            TurnoResponseDto(
                id = 107L,
                fechaHora = "${manana}T15:00:00",
                motivo = "Consulta nutricional e hidratación personalizada",
                estado = "PROGRAMADO",
                nombreMedico = "Lic. Valenzuela",
                nombrePaciente = "María Elena González",
                habitacion = "Hab. 102",
                horaInicio = "15:00",
                horaFin = "16:30"
            ),
            TurnoResponseDto(
                id = 108L,
                fechaHora = "${pasadoManana}T10:00:00",
                motivo = "Seguimiento neurológico integral",
                estado = "PENDIENTE",
                nombreMedico = "Dr. Sánchez",
                nombrePaciente = "Carlos Alberto Rossi",
                habitacion = "Hab. 201",
                horaInicio = "10:00",
                horaFin = "11:30"
            ),
            TurnoResponseDto(
                id = 109L,
                fechaHora = "${dia3}T11:00:00",
                motivo = "Control de laboratorio y ajuste de dosis",
                estado = "PROGRAMADO",
                nombreMedico = "Dra. Martínez",
                nombrePaciente = "Roberto Gómez",
                habitacion = "Hab. 204",
                horaInicio = "11:00",
                horaFin = "12:00"
            )
        )
    }

    private val turnosList = inicializarTurnos()

    fun getTurnosFake(pacienteId: Long): List<TurnoResponseDto> {
        return turnosList.toList()
    }

    fun reprogramarTurno(turnoId: Long, nuevaFechaHora: String, nuevoMotivo: String): Boolean {
        val index = turnosList.indexOfFirst { it.id == turnoId }
        if (index != -1) {
            val actual = turnosList[index]
            turnosList[index] = actual.copy(
                fechaHora = nuevaFechaHora,
                motivo = nuevoMotivo,
                estado = "REPROGRAMADO"
            )
            return true
        }
        return false
    }

    fun cancelarTurno(turnoId: Long, motivoCancelacion: String): Boolean {
        val index = turnosList.indexOfFirst { it.id == turnoId }
        if (index != -1) {
            val actual = turnosList[index]
            turnosList[index] = actual.copy(
                motivo = motivoCancelacion,
                estado = "CANCELADO"
            )
            return true
        }
        return false
    }

    fun agregarTurno(nuevo: TurnoResponseDto) {
        turnosList.add(0, nuevo)
    }

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
