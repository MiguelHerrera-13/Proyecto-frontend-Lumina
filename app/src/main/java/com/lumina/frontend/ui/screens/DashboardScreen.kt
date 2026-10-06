package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.ui.viewmodel.DashboardUiState
import com.lumina.frontend.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    pacienteId: Long = 1L // ID de prueba por ahora para que conecte con la API
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pacienteId) {
        viewModel.loadTurnos(pacienteId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F0FE)) // Fondo muy suave estilo médico
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
                                modifier = Modifier.padding(bottom = 8.dp, top = 24.dp)
                            )
                        }
                        items(turnos) { turno ->
                            TurnoCard(turno)
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

@Composable
fun TurnoCard(turno: TurnoResponseDto) {
    // Implementación de diseño limpio / Glassmorphism
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.90f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (turno.estado == "PENDIENTE" || turno.estado == "PROGRAMADO") Color(0xFFFEF3C7) else Color(0xFFD1FAE5))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = turno.estado ?: "PROGRAMADO",
                        color = if (turno.estado == "PENDIENTE" || turno.estado == "PROGRAMADO") Color(0xFF92400E) else Color(0xFF065F46),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "\uD83D\uDCC5 ${turno.fechaHora}",
                color = Color(0xFF374151),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Motivo: ${turno.motivo ?: "No especificado"}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
