package com.lumina.frontend.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lumina.frontend.data.remote.dto.AlertaResponseDto
import com.lumina.frontend.ui.viewmodel.AlertasUiState
import com.lumina.frontend.ui.viewmodel.DoctorDashboardViewModel

sealed class DoctorTab(val route: String, val title: String, val icon: ImageVector) {
    object Pacientes : DoctorTab("pacientes", "Pacientes", Icons.Default.Person)
    object Turnos : DoctorTab("turnos", "Turnos", Icons.Default.DateRange)
    object Alertas : DoctorTab("alertas", "Alertas", Icons.Default.Warning)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    medicoId: Long = 1L,
    viewModel: DoctorDashboardViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: DoctorTab.Alertas.route

    val tabs = listOf(
        DoctorTab.Pacientes,
        DoctorTab.Turnos,
        DoctorTab.Alertas
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard del Médico",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (currentRoute == DoctorTab.Alertas.route) {
                        IconButton(onClick = { viewModel.cargarAlertas(medicoId) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Recargar alertas"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            if (currentRoute != tab.route) {
                                bottomNavController.navigate(tab.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.title)
                        },
                        label = {
                            Text(text = tab.title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = DoctorTab.Alertas.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DoctorTab.Pacientes.route) {
                TabPacientesContent()
            }
            composable(DoctorTab.Turnos.route) {
                TabTurnosContent()
            }
            composable(DoctorTab.Alertas.route) {
                TabAlertasContent(
                    medicoId = medicoId,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun TabAlertasContent(
    medicoId: Long,
    viewModel: DoctorDashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(medicoId) {
        viewModel.cargarAlertas(medicoId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        when (uiState) {
            is AlertasUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is AlertasUiState.Success -> {
                val alertas = (uiState as AlertasUiState.Success).alertas
                if (alertas.isEmpty()) {
                    Text(
                        text = "No hay alertas clínicas registradas para este médico.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Panel de Alertas Clínicas",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        items(alertas) { alerta ->
                            AlertaCard(alerta = alerta)
                        }
                    }
                }
            }
            is AlertasUiState.Error -> {
                val error = (uiState as AlertasUiState.Error).message
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se pudieron obtener las alertas",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = error, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = { viewModel.cargarAlertas(medicoId) }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
fun AlertaCard(alerta: AlertaResponseDto) {
    val esUrgente = alerta.urgente ||
            alerta.mensajeAlerta.contains("urgente", ignoreCase = true) ||
            alerta.mensajeAlerta.contains("crítica", ignoreCase = true) ||
            alerta.mensajeAlerta.contains("critica", ignoreCase = true) ||
            alerta.mensajeAlerta.contains("alta", ignoreCase = true)

    val colorContenedor = if (esUrgente) Color(0xFFFFEBEE) else Color.White
    val colorBorde = if (esUrgente) Color(0xFFDC2626) else Color(0xFFE5E7EB)
    val colorTextoPaciente = if (esUrgente) Color(0xFFB71C1C) else Color(0xFF1F2937)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorContenedor),
        border = BorderStroke(if (esUrgente) 2.dp else 1.dp, colorBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = if (esUrgente) 6.dp else 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alerta.nombrePaciente ?: "Paciente #${alerta.idReporte ?: alerta.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorTextoPaciente
                )

                if (esUrgente) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚠ ATENCIÓN URGENTE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE5E7EB))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ESTABLE",
                            color = Color(0xFF374151),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alerta.mensajeAlerta,
                fontSize = 15.sp,
                color = if (esUrgente) Color(0xFF7F1D1D) else Color(0xFF4B5563),
                fontWeight = if (esUrgente) FontWeight.SemiBold else FontWeight.Normal
            )

            alerta.fechaHora?.let { fecha ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registrado: $fecha",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TabPacientesContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Listado de Pacientes Asignados al Médico",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun TabTurnosContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Listado de Turnos y Guardias Médicas",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}
