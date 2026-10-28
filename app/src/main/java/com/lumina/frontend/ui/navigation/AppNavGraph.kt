package com.lumina.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.ui.screens.DashboardScreen
import com.lumina.frontend.ui.screens.HomeScreen
import com.lumina.frontend.ui.screens.LoginScreen
import com.lumina.frontend.ui.screens.PatientsScreen
import com.lumina.frontend.ui.screens.RegisterScreen
import com.lumina.frontend.ui.screens.TurnoDetailScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route // Empieza en el inicio
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToPatients = {
                    navController.navigate(Screen.Patients.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Patients.route) {
            PatientsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen()
        }

        composable(route = Screen.TurnoDetail.route) {
            TurnoDetailScreen(
                turno = TurnoResponseDto(
                    id = 1L,
                    fechaHora = "2026-11-15 10:30",
                    motivo = "Control médico general y revisión de medicación.",
                    estado = "PROGRAMADO",
                    nombreMedico = "Dr. Alejandro Gómez"
                ),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
