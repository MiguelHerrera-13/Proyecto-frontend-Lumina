package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.ui.components.TurnoDetailDialog
import com.lumina.frontend.ui.viewmodel.DashboardUiState
import com.lumina.frontend.ui.viewmodel.DashboardViewModel
import com.lumina.frontend.util.CalendarExportHelper
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    pacienteId: Long = 1L // ID de prueba por ahora para que conecte con la API
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estado del diálogo de detalle del turno
    var turnoSeleccionadoParaDetalle by remember { mutableStateOf<TurnoResponseDto?>(null) }

    LaunchedEffect(pacienteId) {
        viewModel.loadTurnos(pacienteId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFE8F0FE)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is DashboardUiState.Success -> {
                    val turnos = (uiState as DashboardUiState.Success).turnos
                    if (turnos.isEmpty()) {
                        Text(
                            text = "No tienes turnos programados en este momento.",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Mis Turnos Médicos",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                                )
                            }
                            items(turnos) { turno ->
                                TurnoCard(
                                    turno = turno,
                                    onVerDetalle = {
                                        turnoSeleccionadoParaDetalle = turno
                                    },
                                    onExportarCalendario = {
                                        CalendarExportHelper.exportarTurnoAlCalendario(
                                            context = context,
                                            turno = turno,
                                            onFeedback = { _, mensaje ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(mensaje)
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                is DashboardUiState.Error -> {
                    val error = (uiState as DashboardUiState.Error).message
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Oh no, ocurrió un error",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadTurnos(pacienteId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }

    // Modal de Detalle de Turno con botón de exportación al calendario nativo
    turnoSeleccionadoParaDetalle?.let { turno ->
        TurnoDetailDialog(
            turno = turno,
            onDismiss = { turnoSeleccionadoParaDetalle = null },
            onCalendarExportFeedback = { _, mensaje ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(mensaje)
                }
            }
        )
    }
}

@Composable
fun TurnoCard(
    turno: TurnoResponseDto,
    onVerDetalle: () -> Unit = {},
    onExportarCalendario: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerDetalle() }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = turno.nombreMedico ?: "Dr. Asignado",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = Color(0xFF111827)
                )
                // Badge de Estado
                val badgeBg = when (turno.estado) {
                    "CANCELADO" -> Color(0xFFFEE2E2)
                    "REPROGRAMADO" -> Color(0xFFE0E7FF)
                    "COMPLETADO" -> Color(0xFFD1FAE5)
                    else -> Color(0xFFFEF3C7)
                }
                val badgeText = when (turno.estado) {
                    "CANCELADO" -> Color(0xFF991B1B)
                    "REPROGRAMADO" -> Color(0xFF3730A3)
                    "COMPLETADO" -> Color(0xFF065F46)
                    else -> Color(0xFF92400E)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = turno.estado ?: "PROGRAMADO",
                        color = badgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "📅 ${turno.fechaHora}",
                color = Color(0xFF374151),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Motivo: ${turno.motivo ?: "No especificado"}",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVerDetalle) {
                    Text("Ver Detalle", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onExportarCalendario,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(text = "🗓️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "A mi calendario",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
