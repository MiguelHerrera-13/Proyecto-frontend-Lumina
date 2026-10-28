package com.lumina.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.ui.screens.DashboardScreen
import com.lumina.frontend.ui.screens.HomeScreen
import com.lumina.frontend.ui.screens.LoginScreen
import com.lumina.frontend.ui.screens.PatientsScreen
import com.lumina.frontend.ui.screens.RegisterScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    // Escuchar expiración global de sesión (refresh token expirado) para redirigir al login
    LaunchedEffect(Unit) {
        SessionManager.sessionExpiredEvent.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

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
                }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = { rol ->
                    val destination = when (rol.uppercase()) {
                        "MEDICO" -> Screen.Patients.route
                        "ADMIN" -> Screen.Patients.route
                        "CUIDADOR" -> Screen.Dashboard.route
                        else -> Screen.Dashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Home.route) { inclusive = true }
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
    }
}
