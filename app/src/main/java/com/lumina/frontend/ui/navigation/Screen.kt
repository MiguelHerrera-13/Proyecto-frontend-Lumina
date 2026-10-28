package com.lumina.frontend.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Patients : Screen("patients")
    object Dashboard : Screen("dashboard")
    object Ingesta : Screen("ingesta") // Módulo de Ingesta e Hidratación
    object PatientDetail : Screen("patient_detail/{pacienteId}") {
        fun createRoute(pacienteId: Long): String = "patient_detail/$pacienteId"
    }
}
