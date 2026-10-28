package com.lumina.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lumina.frontend.data.local.FakePatientDetailDataSource
import com.lumina.frontend.ui.screens.CrearTurnoScreen
import com.lumina.frontend.ui.screens.DashboardScreen
import com.lumina.frontend.ui.screens.HomeScreen
import com.lumina.frontend.ui.screens.IngestaScreen
import com.lumina.frontend.ui.screens.LoginScreen
import com.lumina.frontend.ui.screens.PatientDetailScreen
import com.lumina.frontend.ui.screens.PatientsScreen
import com.lumina.frontend.ui.screens.RegisterScreen
import com.lumina.frontend.ui.screens.ReporteDiarioScreen
import com.lumina.frontend.ui.screens.TurnoDetailScreen

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
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                },
                onNavigateToCrearTurno = {
                    navController.navigate(Screen.CrearTurno.route)
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
                },
                onPatientClick = { id ->
                    navController.navigate(Screen.PatientDetail.createRoute(id))
                }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCrearTurno = {
                    navController.navigate(Screen.CrearTurno.route)
                },
                onNavigateToTurnoDetail = { turnoId ->
                    navController.navigate(Screen.TurnoDetail.createRoute(turnoId))
                },
                onNavigateToCrearReporte = { turnoId ->
                    navController.navigate(Screen.CrearReporteDiario.createRoute(turnoId))
                }
            )
        }

        composable(route = Screen.CrearTurno.route) {
            CrearTurnoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTurnoCreado = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Ingesta.route) {
            IngestaScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TurnoDetail.route,
            arguments = listOf(
                navArgument("turnoId") {
                    type = NavType.LongType
                    defaultValue = 101L
                }
            )
        ) { backStackEntry ->
            val turnoId = backStackEntry.arguments?.getLong("turnoId") ?: 101L
            val turnos = FakePatientDetailDataSource.getTurnosFake(1L)
            val turno = turnos.find { it.id == turnoId } ?: turnos.first()

            TurnoDetailScreen(
                turno = turno,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinalizarYCrearReporte = { id ->
                    navController.navigate(Screen.CrearReporteDiario.createRoute(id))
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

        composable(
            route = Screen.CrearReporteDiario.route,
            arguments = listOf(
                navArgument("turnoId") {
                    type = NavType.LongType
                    defaultValue = 1L
                }
            )
        ) { backStackEntry ->
            val turnoId = backStackEntry.arguments?.getLong("turnoId") ?: 1L
            ReporteDiarioScreen(
                idTurno = turnoId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
