package com.lumina.frontend.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Patients : Screen("patients")
    object Dashboard : Screen("dashboard")
    object Ingesta : Screen("ingesta") // Módulo de Ingesta e Hidratación
    object TurnoDetail : Screen("turno_detail") // Detalle de Turno y Exportación a Calendario
    object CrearTurno : Screen("crear_turno") // Solicitud y Creación de Turnos Médicos (Issue #21)
    object PatientDetail : Screen("patient_detail/{pacienteId}") {
        fun createRoute(pacienteId: Long): String = "patient_detail/$pacienteId"
    }
    object CrearReporteDiario : Screen("crear_reporte_diario/{turnoId}") {
        fun createRoute(turnoId: Long): String = "crear_reporte_diario/$turnoId"
    }
}
