package com.lumina.frontend.data.local

import com.lumina.frontend.data.model.Patient

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
}