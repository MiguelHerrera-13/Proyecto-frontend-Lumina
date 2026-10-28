package com.lumina.frontend.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Patients : Screen("patients")
    object Dashboard : Screen("dashboard")
    object Ingesta : Screen("ingesta") // Módulo de Ingesta e Hidratación
    object CrearTurno : Screen("crear_turno") // Solicitud y Creación de Turnos Médicos (Issue #21)
    object TurnoDetail : Screen("turno_detail/{turnoId}") { // Detalle de Turno y Acciones de Estado (Issue #22)
        fun createRoute(turnoId: Long): String = "turno_detail/$turnoId"
    }
    object PatientDetail : Screen("patient_detail/{pacienteId}") {
        fun createRoute(pacienteId: Long): String = "patient_detail/$pacienteId"
    }
    object CrearReporteDiario : Screen("crear_reporte_diario/{turnoId}") {
        fun createRoute(turnoId: Long): String = "crear_reporte_diario/$turnoId"
    }
}
