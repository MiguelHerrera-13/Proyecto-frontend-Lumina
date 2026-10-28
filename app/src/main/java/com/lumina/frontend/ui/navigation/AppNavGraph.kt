package com.lumina.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lumina.frontend.ui.screens.DashboardScreen
import com.lumina.frontend.ui.screens.HomeScreen
import com.lumina.frontend.ui.screens.IngestaScreen
import com.lumina.frontend.ui.screens.LoginScreen
import com.lumina.frontend.ui.screens.PatientDetailScreen
import com.lumina.frontend.ui.screens.PatientsScreen
import com.lumina.frontend.ui.screens.RegisterScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
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
                onNavigateToIngesta = {
                    navController.navigate(Screen.Ingesta.route)
                }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Patients.route) {
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
                },
                onPatientClick = { id ->
                    navController.navigate(Screen.PatientDetail.createRoute(id))
                }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen()
        }

        composable(route = Screen.Ingesta.route) {
            IngestaScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.PatientDetail.route,
            arguments = listOf(
                navArgument("pacienteId") {
                    type = NavType.LongType
                    defaultValue = 1L
                }
            )
        ) { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getLong("pacienteId") ?: 1L
            PatientDetailScreen(
                pacienteId = pacienteId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
