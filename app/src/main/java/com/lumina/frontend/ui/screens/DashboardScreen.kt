package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.ui.components.TurnoFiltrosBottomSheet
import com.lumina.frontend.ui.viewmodel.DashboardUiState
import com.lumina.frontend.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    pacienteId: Long = 1L
) {
    val uiState by viewModel.uiState.collectAsState()
    val filtros by viewModel.filtros.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var mostrarFiltrosSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pacienteId) {
        viewModel.loadTurnos(pacienteId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Turnos Médicos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (filtros.tieneFiltrosActivos) {
                            Text(
                                text = "Filtros activos (${filtros.cantidadFiltrosActivos})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // BOTÓN EN LA BARRA SUPERIOR PARA DESPLEGAR EL MODAL BOTTOM SHEET
                    IconButton(onClick = { mostrarFiltrosSheet = true }) {
                        BadgedBox(
                            badge = {
                                if (filtros.tieneFiltrosActivos) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text("${filtros.cantidadFiltrosActivos}")
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "🌪️",
                                fontSize = 20.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        containerColor = Color(0xFFF1F5F9)
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
                    val successState = uiState as DashboardUiState.Success
                    val turnos = successState.turnos

                    Column(modifier = Modifier.fillMaxSize()) {
                        // CHIPS INFORMATIVOS DE FILTROS ACTIVOS EN CABECERA
                        if (filtros.tieneFiltrosActivos) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Chip para limpiar todos
                                item {
                                    SuggestionChip(
                                        onClick = { viewModel.limpiarFiltros() },
                                        label = { Text("Quitar todos los filtros", fontWeight = FontWeight.Bold) }
                                    )
                                }

                                // Chips de estados
                                items(filtros.estadosSeleccionados.toList()) { estado ->
                                    InputChip(
                                        selected = true,
                                        onClick = { viewModel.removerEstado(estado) },
                                        label = { Text(estado) },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover $estado",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }

                                // Chip de profesional
                                if (filtros.profesionalQuery.isNotBlank()) {
                                    item {
                                        InputChip(
                                            selected = true,
                                            onClick = { viewModel.removerFiltroProfesional() },
                                            label = { Text("Médico: ${filtros.profesionalQuery}") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remover filtro médico",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }

                                // Chip de fechas
                                if (filtros.fechaDesde != null || filtros.fechaHasta != null) {
                                    item {
                                        val label = "${filtros.fechaDesde ?: "..."} a ${filtros.fechaHasta ?: "..."}"
                                        InputChip(
                                            selected = true,
                                            onClick = { viewModel.removerFiltroFechas() },
                                            label = { Text("Fechas: $label") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remover filtro fechas",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // LISTA DE TURNOS O ESTADO VACÍO
                        if (turnos.isEmpty()) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "🔍", fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (filtros.tieneFiltrosActivos)
                                            "No se encontraron turnos con los filtros aplicados."
                                        else
                                            "No tienes turnos programados en este momento.",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (filtros.tieneFiltrosActivos)
                                            "Prueba modificando o eliminando los filtros seleccionados."
                                        else
                                            "Cuando agendes nuevos turnos, aparecerán en este panel.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                    if (filtros.tieneFiltrosActivos) {
                                        Spacer(modifier = Modifier.height(18.dp))
                                        Button(
                                            onClick = { viewModel.limpiarFiltros() },
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Restablecer todos los filtros")
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(turnos) { turno ->
                                    TurnoCard(turno)
                                }
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
                            text = "Ocurrió un error al cargar los turnos",
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

    // MODAL BOTTOM SHEET DE FILTROS AVANZADOS
    if (mostrarFiltrosSheet) {
        TurnoFiltrosBottomSheet(
            filtrosActuales = filtros,
            onDismissRequest = { mostrarFiltrosSheet = false },
            onAplicarFiltros = { nuevosFiltros ->
                viewModel.aplicarFiltros(nuevosFiltros)
                coroutineScope.launch {
                    val cantidad = nuevosFiltros.cantidadFiltrosActivos
                    val msg = if (cantidad > 0) "Filtros aplicados ($cantidad activos)" else "Filtros restablecidos"
                    snackbarHostState.showSnackbar(msg)
                }
            },
            onLimpiarFiltros = {
                viewModel.limpiarFiltros()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Filtros restablecidos a valores iniciales.")
                }
            }
        )
    }
}

@Composable
fun TurnoCard(turno: TurnoResponseDto) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
        }
    }
}
