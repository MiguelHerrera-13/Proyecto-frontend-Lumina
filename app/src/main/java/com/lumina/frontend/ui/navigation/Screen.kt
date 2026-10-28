package com.lumina.frontend.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Patients : Screen("patients")
    object Dashboard : Screen("dashboard") // Agregado para el Dashboard del paciente
    object TurnoDetail : Screen("turno_detail") // Detalle de Turno y Exportación a Calendario
}
