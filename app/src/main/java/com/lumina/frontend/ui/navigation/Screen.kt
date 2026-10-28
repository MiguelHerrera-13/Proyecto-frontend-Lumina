package com.lumina.frontend.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Patients : Screen("patients")
    object Dashboard : Screen("dashboard") // Dashboard del cuidador/paciente
    object DashboardMedico : Screen("dashboard_medico") // Dashboard para el rol MEDICO
    object DashboardAdmin : Screen("dashboard_admin") // Dashboard para el rol ADMIN
}
