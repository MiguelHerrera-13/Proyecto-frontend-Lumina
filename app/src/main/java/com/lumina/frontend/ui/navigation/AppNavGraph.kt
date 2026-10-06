package com.lumina.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumina.frontend.ui.screens.HomeScreen
import com.lumina.frontend.ui.screens.LoginScreen
import com.lumina.frontend.ui.screens.PatientsScreen
import com.lumina.frontend.ui.screens.RegisterScreen
import com.lumina.frontend.ui.screens.DashboardScreen
import com.lumina.frontend.ui.screens.DoctorDashboardScreen

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
                    // Por facilidad de prueba, mandemos el botón de pacientes al Dashboard temporalmente
                    navController.navigate(Screen.Dashboard.route)
                },
                onNavigateToDoctorDashboard = {
                    navController.navigate(Screen.DoctorDashboard.route)
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

        composable(route = Screen.DoctorDashboard.route) {
            DoctorDashboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
