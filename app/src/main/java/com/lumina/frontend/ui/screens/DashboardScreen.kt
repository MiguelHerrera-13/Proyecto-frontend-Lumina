package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.lumina.frontend.ui.components.AccionTurno
import com.lumina.frontend.ui.components.CalendarioTurnosComponent
import com.lumina.frontend.ui.components.TurnoAccionDialog
import com.lumina.frontend.ui.components.TurnoDetailDialog
import com.lumina.frontend.ui.components.TurnoFiltrosBottomSheet
import com.lumina.frontend.ui.components.extraerFechaIso
import com.lumina.frontend.ui.viewmodel.DashboardUiState
import com.lumina.frontend.ui.viewmodel.DashboardViewModel
import com.lumina.frontend.util.CalendarExportHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    pacienteId: Long = 1L,
    onNavigateBack: () -> Unit = {},
    onNavigateToCrearTurno: () -> Unit = {},
    onNavigateToTurnoDetail: (Long) -> Unit = {},
    onNavigateToCrearReporte: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val filtros by viewModel.filtros.collectAsState()
    val fechaCalendario by viewModel.fechaSeleccionadaCalendario.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estado del diálogo de detalle del turno
    var turnoSeleccionadoParaDetalle by remember { mutableStateOf<TurnoResponseDto?>(null) }
    // Estado del diálogo modal de cancelación / reprogramación
    var turnoSeleccionadoParaAccion by remember { mutableStateOf<Pair<Long, AccionTurno>?>(null) }
    // Estado del bottom sheet de filtros
    var mostrarFiltrosSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pacienteId) {
        viewModel.loadTurnos(pacienteId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Mis Turnos Médicos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (filtros.tieneFiltrosActivos || !fechaCalendario.isNullOrBlank()) {
                            Text(
                                text = "Filtros activos (${filtros.cantidadFiltrosActivos + if (!fechaCalendario.isNullOrBlank()) 1 else 0})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Botón directo para solicitar/agendar un nuevo turno (Issue #21)
                    IconButton(onClick = onNavigateToCrearTurno) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agendar Turno",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Botón superior para desplegar ModalBottomSheet de filtros
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
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtros",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCrearTurno,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo Turno") },
                text = { Text("Nuevo Turno", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    val todosLosTurnos = successState.todosLosTurnos.ifEmpty { turnos }

                    Column(modifier = Modifier.fillMaxSize()) {
                        // 1. COMPONENTE DE CALENDARIO INTERACTIVO SEMANAL/MENSUAL (Issue Frontend #20)
                        CalendarioTurnosComponent(
                            turnos = todosLosTurnos,
                            fechaSeleccionadaIso = fechaCalendario,
                            onFechaSeleccionada = { fechaIso ->
                                viewModel.seleccionarFechaCalendario(fechaIso)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. CHIPS INFORMATIVOS DE FILTROS ACTIVOS EN CABECERA
                        if (filtros.tieneFiltrosActivos || !fechaCalendario.isNullOrBlank()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    SuggestionChip(
                                        onClick = { viewModel.limpiarFiltros() },
                                        label = { Text("Quitar todos los filtros", fontWeight = FontWeight.Bold) }
                                    )
                                }

                                if (!fechaCalendario.isNullOrBlank()) {
                                    item {
                                        InputChip(
                                            selected = true,
                                            onClick = { viewModel.seleccionarFechaCalendario(null) },
                                            label = { Text("Día: $fechaCalendario") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remover filtro de día",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }

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

                                if (filtros.fechaDesde != null || filtros.fechaHasta != null) {
                                    item {
                                        val label = "${filtros.fechaDesde ?: "..."} a ${filtros.fechaHasta ?: "..."}"
                                        InputChip(
                                            selected = true,
                                            onClick = { viewModel.removerFiltroFechas() },
                                            label = { Text("Rango: $label") },
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

                        // 3. LISTA DE TURNOS O ESTADO VACÍO
                        if (turnos.isEmpty()) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(46.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val tieneFiltroDia = !fechaCalendario.isNullOrBlank()
                                    Text(
                                        text = when {
                                            tieneFiltroDia -> "No hay turnos para la fecha seleccionada."
                                            filtros.tieneFiltrosActivos -> "No se encontraron turnos con los filtros aplicados."
                                            else -> "No tienes turnos programados en este momento."
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = when {
                                            tieneFiltroDia -> "Puedes seleccionar otro día en el calendario o ver todos los turnos disponibles."
                                            filtros.tieneFiltrosActivos -> "Prueba modificando o eliminando los filtros seleccionados."
                                            else -> "Cuando agendes nuevos turnos, aparecerán en este panel."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    if (tieneFiltroDia || filtros.tieneFiltrosActivos) {
                                        Spacer(modifier = Modifier.height(18.dp))
                                        Button(
                                            onClick = {
                                                viewModel.seleccionarFechaCalendario(null)
                                                viewModel.limpiarFiltros()
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(if (tieneFiltroDia) "Ver todos los días" else "Restablecer todos los filtros")
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(18.dp))
                                        Button(
                                            onClick = onNavigateToCrearTurno,
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Agendar un nuevo turno", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 80.dp), // Espacio para el FAB
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = turnos,
                                    key = { it.id }
                                ) { turno ->
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
                                        },
                                        onCancelar = {
                                            turnoSeleccionadoParaAccion = Pair(turno.id, AccionTurno.CANCELAR)
                                        },
                                        onReprogramar = {
                                            turnoSeleccionadoParaAccion = Pair(turno.id, AccionTurno.REPROGRAMAR)
                                        }
                                    )
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

    // Modal de Detalle de Turno
    turnoSeleccionadoParaDetalle?.let { turno ->
        TurnoDetailDialog(
            turno = turno,
            onDismiss = { turnoSeleccionadoParaDetalle = null },
            onIniciarTurno = { id ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Turno #$id iniciado. Ahora se encuentra EN CURSO.")
                }
            },
            onFinalizarYCrearReporte = { id ->
                turnoSeleccionadoParaDetalle = null
                onNavigateToCrearReporte(id)
            },
            onVerPantallaCompleta = { id ->
                turnoSeleccionadoParaDetalle = null
                onNavigateToTurnoDetail(id)
            },
            onCancelarTurno = {
                val id = turno.id
                turnoSeleccionadoParaDetalle = null
                turnoSeleccionadoParaAccion = Pair(id, AccionTurno.CANCELAR)
            },
            onReprogramarTurno = {
                val id = turno.id
                turnoSeleccionadoParaDetalle = null
                turnoSeleccionadoParaAccion = Pair(id, AccionTurno.REPROGRAMAR)
            },
            onCalendarExportFeedback = { _, mensaje ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(mensaje)
                }
            }
        )
    }

    // Modal de Cancelación / Reprogramación
    turnoSeleccionadoParaAccion?.let { (id, accion) ->
        TurnoAccionDialog(
            turnoId = id,
            accionInicial = accion,
            onDismiss = {
                turnoSeleccionadoParaAccion = null
            },
            onSuccess = { mensajeExito ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(mensajeExito)
                }
                viewModel.loadTurnos(pacienteId)
            }
        )
    }

    // Modal Bottom Sheet de Filtros Avanzados
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
fun TurnoCard(
    turno: TurnoResponseDto,
    onVerDetalle: () -> Unit = {},
    onExportarCalendario: () -> Unit = {},
    onCancelar: () -> Unit = {},
    onReprogramar: () -> Unit = {}
) {
    val isCancelado = turno.estado == "CANCELADO"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerDetalle() }
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = turno.nombreMedico ?: "Dr. Asignado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!turno.nombrePaciente.isNullOrBlank()) {
                        Text(
                            text = "Paciente: ${turno.nombrePaciente}${if (!turno.habitacion.isNullOrBlank()) " (${turno.habitacion})" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                // Badge de Estado con Color Distintivo
                val badgeBg = when (turno.estado) {
                    "CANCELADO" -> MaterialTheme.colorScheme.errorContainer
                    "REPROGRAMADO" -> MaterialTheme.colorScheme.secondaryContainer
                    "COMPLETADO" -> Color(0xFF10B981).copy(alpha = 0.2f)
                    "EN_CURSO" -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
                val badgeText = when (turno.estado) {
                    "CANCELADO" -> MaterialTheme.colorScheme.onErrorContainer
                    "REPROGRAMADO" -> MaterialTheme.colorScheme.onSecondaryContainer
                    "COMPLETADO" -> Color(0xFF10B981)
                    "EN_CURSO" -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onTertiaryContainer
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = turno.estado ?: "PROGRAMADO",
                        color = badgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            val horarioDisplay = when {
                !turno.horaInicio.isNullOrBlank() && !turno.horaFin.isNullOrBlank() ->
                    "${extraerFechaIso(turno.fechaHora) ?: turno.fechaHora} • ${turno.horaInicio} - ${turno.horaFin} hs"
                else -> turno.fechaHora
            }
            Text(
                text = "📅 $horarioDisplay",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Motivo: ${turno.motivo ?: "No especificado"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Fila de acciones principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onExportarCalendario,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "A mi calendario",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (!isCancelado) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onCancelar,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        OutlinedButton(
                            onClick = onReprogramar,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Reprogramar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
