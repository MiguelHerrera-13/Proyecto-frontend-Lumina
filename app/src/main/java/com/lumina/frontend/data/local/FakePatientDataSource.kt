package com.lumina.frontend.data.local

import com.lumina.frontend.data.model.Patient
import com.lumina.frontend.data.remote.dto.PacienteResponseDto

object FakePatientDataSource {

    fun getPatients(): List<Patient> {
        return listOf(
            Patient(
                id = 1,
                name = "María Elena González",
                age = 78,
                room = "Hab. 204",
                condition = "Acompañamiento diurno",
                status = "Estable"
            ),
            Patient(
                id = 2,
                name = "Carlos Alberto Ramírez",
                age = 82,
                room = "Hab. 112",
                condition = "Rehabilitación motriz",
                status = "En reposo"
            ),
            Patient(
                id = 3,
                name = "Lucía Fernández",
                age = 71,
                room = "Hab. 305",
                condition = "Control posoperatorio",
                status = "Observación"
            ),
            Patient(
                id = 4,
                name = "Jorge Eduardo Méndez",
                age = 85,
                room = "Hab. 108",
                condition = "Monitoreo cognitivo",
                status = "Estable"
            )
        )
    }

    fun getPatientsDto(): List<PacienteResponseDto> {
        return listOf(
            PacienteResponseDto(
                idPaciente = 1L,
                nombreCompleto = "María Elena González",
                fechaNacimiento = "1946-04-12",
                diagnosticoFase = "Fase 2 - Moderada",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 2L,
                nombreCompleto = "Carlos Alberto Ramírez",
                fechaNacimiento = "1942-09-25",
                diagnosticoFase = "Fase 1 - Inicial",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 3L,
                nombreCompleto = "Lucía Fernández",
                fechaNacimiento = "1953-11-03",
                diagnosticoFase = "Fase 3 - Avanzada",
                activo = false
            ),
            PacienteResponseDto(
                idPaciente = 4L,
                nombreCompleto = "Jorge Eduardo Méndez",
                fechaNacimiento = "1939-01-18",
                diagnosticoFase = "Monitoreo cognitivo",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 5L,
                nombreCompleto = "Ana Sofía Benítez",
                fechaNacimiento = "1960-07-22",
                diagnosticoFase = "Rehabilitación motriz",
                activo = false
            ),
            PacienteResponseDto(
                idPaciente = 6L,
                nombreCompleto = "Roberto Gómez",
                fechaNacimiento = "1950-03-15",
                diagnosticoFase = "Control posoperatorio",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 7L,
                nombreCompleto = "Marta Susana Ríos",
                fechaNacimiento = "1948-12-05",
                diagnosticoFase = "Fase 1 - Inicial",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 8L,
                nombreCompleto = "Esteban Daniel López",
                fechaNacimiento = "1955-08-30",
                diagnosticoFase = "Control posoperatorio",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 9L,
                nombreCompleto = "Beatriz Noemí Castro",
                fechaNacimiento = "1944-05-14",
                diagnosticoFase = "Fase 2 - Moderada",
                activo = false
            ),
            PacienteResponseDto(
                idPaciente = 10L,
                nombreCompleto = "Héctor Rolando Morales",
                fechaNacimiento = "1938-10-21",
                diagnosticoFase = "Fase 3 - Avanzada",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 11L,
                nombreCompleto = "Silvia Graciela Vargas",
                fechaNacimiento = "1952-02-18",
                diagnosticoFase = "Monitoreo cognitivo",
                activo = true
            ),
            PacienteResponseDto(
                idPaciente = 12L,
                nombreCompleto = "Guillermo Andrés Silva",
                fechaNacimiento = "1947-09-09",
                diagnosticoFase = "Rehabilitación motriz",
                activo = false
            )
        )
    }
}
