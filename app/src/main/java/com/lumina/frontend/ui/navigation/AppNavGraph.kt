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
    }
}
