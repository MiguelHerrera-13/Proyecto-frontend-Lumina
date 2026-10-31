package com.lumina.frontend.data.local

import com.lumina.frontend.data.model.Patient
import com.lumina.frontend.data.remote.dto.PacienteResponseDto

object FakePatientDataSource {

    // Lista mutable reactiva en memoria para permitir altas y ediciones inmediatas de pacientes
    private val fakePatientsList = mutableListOf(
        PacienteResponseDto(
            idPaciente = 1L,
            nombreCompleto = "Roberto Gómez",
            fechaNacimiento = "1948-03-12",
            diagnosticoFase = "Alzheimer (Fase 1 - Inicial)",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 2L,
            nombreCompleto = "Carlos Alberto Ramírez",
            fechaNacimiento = "1942-09-25",
            diagnosticoFase = "Parkinson (Fase 1 - Inicial)",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 3L,
            nombreCompleto = "María Elena González",
            fechaNacimiento = "1946-04-12",
            diagnosticoFase = "Hipertensión Severa y Control Cardiológico",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 4L,
            nombreCompleto = "Jorge Eduardo Méndez",
            fechaNacimiento = "1939-01-18",
            diagnosticoFase = "Deterioro Cognitivo y Pérdida de Memoria",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 5L,
            nombreCompleto = "Ana Sofía Benítez",
            fechaNacimiento = "1960-07-22",
            diagnosticoFase = "Rehabilitación Motriz Post-ACV (Fase 2)",
            activo = false
        ),
        PacienteResponseDto(
            idPaciente = 6L,
            nombreCompleto = "Lucía Fernández",
            fechaNacimiento = "1953-11-03",
            diagnosticoFase = "Demencia Vascular (Fase 3 - Avanzada)",
            activo = false
        ),
        PacienteResponseDto(
            idPaciente = 7L,
            nombreCompleto = "Marta Susana Ríos",
            fechaNacimiento = "1948-12-05",
            diagnosticoFase = "Diabetes Tipo 2 y Cuidados Diurnos",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 8L,
            nombreCompleto = "Esteban Daniel López",
            fechaNacimiento = "1955-08-30",
            diagnosticoFase = "Artritis Reumatoide y Movilidad Reducida",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 9L,
            nombreCompleto = "Beatriz Noemí Castro",
            fechaNacimiento = "1944-05-14",
            diagnosticoFase = "EPOC y Oxigenoterapia Domiciliaria",
            activo = false
        ),
        PacienteResponseDto(
            idPaciente = 10L,
            nombreCompleto = "Héctor Rolando Morales",
            fechaNacimiento = "1938-10-21",
            diagnosticoFase = "Fractura de Cadera en Fisioterapia",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 11L,
            nombreCompleto = "Silvia Graciela Vargas",
            fechaNacimiento = "1952-02-18",
            diagnosticoFase = "Insuficiencia Cardíaca y Monitoreo",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 12L,
            nombreCompleto = "Guillermo Andrés Silva",
            fechaNacimiento = "1947-09-09",
            diagnosticoFase = "Esclerosis Lateral en Fase Inicial",
            activo = false
        ),
        PacienteResponseDto(
            idPaciente = 13L,
            nombreCompleto = "Clara Domínguez",
            fechaNacimiento = "1951-06-19",
            diagnosticoFase = "Monitoreo Posquirúrgico Traumatológico",
            activo = true
        ),
        PacienteResponseDto(
            idPaciente = 14L,
            nombreCompleto = "Pedro Ignacio Valenzuela",
            fechaNacimiento = "1940-11-28",
            diagnosticoFase = "Demencia por Cuerpos de Lewy (Fase 2)",
            activo = true
        )
    )

    fun getPatients(): List<Patient> {
        return listOf(
            Patient(
                id = 1,
                name = "Roberto Gómez",
                age = 78,
                room = "Hab. 204",
                condition = "Alzheimer (Fase 1)",
                status = "Estable"
            ),
            Patient(
                id = 2,
                name = "Carlos Alberto Ramírez",
                age = 82,
                room = "Hab. 112",
                condition = "Parkinson (Fase 1)",
                status = "En reposo"
            ),
            Patient(
                id = 3,
                name = "María Elena González",
                age = 78,
                room = "Hab. 305",
                condition = "Hipertensión Severa",
                status = "Observación"
            ),
            Patient(
                id = 4,
                name = "Jorge Eduardo Méndez",
                age = 85,
                room = "Hab. 108",
                condition = "Deterioro Cognitivo",
                status = "Estable"
            )
        )
    }

    fun getPatientsDto(): List<PacienteResponseDto> {
        return fakePatientsList.toList()
    }

    fun addPatient(
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String?
    ): PacienteResponseDto {
        val nextId = (fakePatientsList.maxOfOrNull { it.idPaciente } ?: 0L) + 1L
        val newPatient = PacienteResponseDto(
            idPaciente = nextId,
            nombreCompleto = nombreCompleto.trim(),
            fechaNacimiento = fechaNacimiento,
            diagnosticoFase = diagnosticoFase?.ifBlank { "Control clínico general" } ?: "Control clínico general",
            activo = true
        )
        fakePatientsList.add(0, newPatient) // Añadir al inicio para visibilidad inmediata
        return newPatient
    }

    fun updatePatient(
        id: Long,
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String
    ): PacienteResponseDto? {
        val index = fakePatientsList.indexOfFirst { it.idPaciente == id }
        if (index != -1) {
            val updated = fakePatientsList[index].copy(
                nombreCompleto = nombreCompleto.trim(),
                fechaNacimiento = fechaNacimiento.trim(),
                diagnosticoFase = diagnosticoFase.trim()
            )
            fakePatientsList[index] = updated
            return updated
        }
        return null
    }
}
