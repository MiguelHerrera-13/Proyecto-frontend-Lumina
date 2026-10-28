@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.lumina.frontend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.data.model.ReporteDiarioPaso
import com.lumina.frontend.data.model.ReporteGeneralData
import com.lumina.frontend.data.model.ReporteHigieneData
import com.lumina.frontend.data.model.ReporteIncidentesData
import com.lumina.frontend.data.model.ReporteIngestaData
import com.lumina.frontend.ui.viewmodel.ReporteDiarioUiState
import com.lumina.frontend.ui.viewmodel.ReporteDiarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteDiarioScreen(
    idTurno: Long,
    onBack: () -> Unit,
    viewModel: ReporteDiarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDialogoSalir by remember { mutableStateOf(false) }

    LaunchedEffect(idTurno) {
        viewModel.inicializarTurno(idTurno)
    }

    if (mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSalir = false },
            title = { Text("¿Deseas salir del reporte?") },
            text = {
                Text("Los cambios realizados se conservan protegidos en el borrador temporal en memoria para que no pierdas tu avance.")
            },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogoSalir = false
                    onBack()
                }) {
                    Text("Conservar y salir")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.descartarBorrador()
                    mostrarDialogoSalir = false
                    onBack()
                }) {
                    Text("Descartar borrador", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reporte Diario del Turno",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Turno #$idTurno",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { mostrarDialogoSalir = true }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.guardarBorradorManual() }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar borrador")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            ReporteBottomBar(
                pasoActual = uiState.pasoActual,
                isLoading = uiState.isLoading,
                onAnterior = { viewModel.retrocederPaso() },
                onSiguiente = { viewModel.avanzarPaso() },
                onFinalizar = { viewModel.finalizarReporte() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Criterio 2: Indicador de progreso de llenado del reporte
            ReporteProgresoHeader(
                pasoActual = uiState.pasoActual,
                porcentajeProgreso = uiState.porcentajeProgreso,
                borradorGuardado = uiState.borradorGuardadoEnMemoria,
                onPasoClick = { viewModel.irAPaso(it) }
            )

            // Mensajes de éxito o feedback
            uiState.mensajeFeedback?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Criterio 1: Interfaz estructurada que evita sobrecargar la pantalla con campos continuos (AnimatedContent)
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = uiState.pasoActual,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "StepperSectionAnimation"
                ) { paso ->
                    when (paso) {
                        ReporteDiarioPaso.GENERAL -> SeccionGeneralContent(
                            data = uiState.general,
                            onUpdate = { viewModel.updateGeneral(it) }
                        )
                        ReporteDiarioPaso.INGESTA -> SeccionIngestaContent(
                            data = uiState.ingesta,
                            onUpdate = { viewModel.updateIngesta(it) }
                        )
                        ReporteDiarioPaso.HIGIENE -> SeccionHigieneContent(
                            data = uiState.higiene,
                            onUpdate = { viewModel.updateHigiene(it) }
                        )
                        ReporteDiarioPaso.INCIDENTES -> SeccionIncidentesContent(
                            data = uiState.incidentes,
                            onUpdate = { viewModel.updateIncidentes(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReporteProgresoHeader(
    pasoActual: ReporteDiarioPaso,
    porcentajeProgreso: Float,
    borradorGuardado: Boolean,
    onPasoClick: (ReporteDiarioPaso) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del Reporte: ${(porcentajeProgreso * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Criterio 3: Badge de guardado de borrador en memoria
                if (borradorGuardado) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✓ Borrador en memoria",
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = porcentajeProgreso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Stepper de Pasos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReporteDiarioPaso.values().forEach { paso ->
                    val isCurrent = paso == pasoActual
                    val isPassed = paso.numero < pasoActual.numero

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onPasoClick(paso) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        isPassed -> Color(0xFF2E7D32)
                                        else -> MaterialTheme.colorScheme.outlineVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPassed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = paso.numero.toString(),
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = paso.titulo,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeccionGeneralContent(
    data: ReporteGeneralData,
    onUpdate: (ReporteGeneralData) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionTitle(
            titulo = "Paso 1: Signos Vitales y Estado General",
            subtitulo = "Registra las constantes vitales observadas en el inicio del turno."
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = data.temperatura,
                onValueChange = { onUpdate(data.copy(temperatura = it)) },
                label = { Text("Temperatura (°C)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = data.presionArterial,
                onValueChange = { onUpdate(data.copy(presionArterial = it)) },
                label = { Text("Presión Arterial") },
                placeholder = { Text("120/80") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Text(
            text = "Estado de Ánimo Predominante",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val animos = listOf("Tranquilo", "Animado", "Desorientado", "Ansioso")
            animos.forEach { animo ->
                FilterChip(
                    selected = data.estadoAnimo == animo,
                    onClick = { onUpdate(data.copy(estadoAnimo = animo)) },
                    label = { Text(animo) }
                )
            }
        }

        Text(
            text = "Nivel de Movilidad",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val movilidades = listOf("Autónomo", "Asistido", "En cama")
            movilidades.forEach { mov ->
                FilterChip(
                    selected = data.nivelMovilidad == mov,
                    onClick = { onUpdate(data.copy(nivelMovilidad = mov)) },
                    label = { Text(mov) }
                )
            }
        }

        OutlinedTextField(
            value = data.observacionesGenerales,
            onValueChange = { onUpdate(data.copy(observacionesGenerales = it)) },
            label = { Text("Observaciones Generales del Turno") },
            placeholder = { Text("Detalla cualquier síntoma o conducta particular...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SeccionIngestaContent(
    data: ReporteIngestaData,
    onUpdate: (ReporteIngestaData) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionTitle(
            titulo = "Paso 2: Ingesta Nutricional e Hidratación",
            subtitulo = "Marca las tomas completadas y registra el consumo de líquidos."
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Comidas Realizadas",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                CheckboxItem(
                    label = "Desayuno",
                    checked = data.desayunoCompletado,
                    onCheckedChange = { onUpdate(data.copy(desayunoCompletado = it)) }
                )
                CheckboxItem(
                    label = "Almuerzo",
                    checked = data.almuerzoCompletado,
                    onCheckedChange = { onUpdate(data.copy(almuerzoCompletado = it)) }
                )
                CheckboxItem(
                    label = "Merienda",
                    checked = data.meriendaCompletada,
                    onCheckedChange = { onUpdate(data.copy(meriendaCompletada = it)) }
                )
                CheckboxItem(
                    label = "Cena",
                    checked = data.cenaCompletada,
                    onCheckedChange = { onUpdate(data.copy(cenaCompletada = it)) }
                )
            }
        }

        Text(
            text = "Agua / Líquidos Consumidos: ${data.aguaMlConsumida} ml",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Slider(
            value = data.aguaMlConsumida.toFloat(),
            onValueChange = { onUpdate(data.copy(aguaMlConsumida = it.toInt())) },
            valueRange = 0f..3000f,
            steps = 29
        )

        Text(
            text = "Nivel de Apetito",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val apetitos = listOf("Excelente", "Normal", "Escaso", "Rechazó")
            apetitos.forEach { apetito ->
                FilterChip(
                    selected = data.apetitoNivel == apetito,
                    onClick = { onUpdate(data.copy(apetitoNivel = apetito)) },
                    label = { Text(apetito) }
                )
            }
        }

        OutlinedTextField(
            value = data.observacionesIngesta,
            onValueChange = { onUpdate(data.copy(observacionesIngesta = it)) },
            label = { Text("Observaciones de Nutrición") },
            placeholder = { Text("Ej: ingirió toda la sopa, suplemento proteico...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SeccionHigieneContent(
    data: ReporteHigieneData,
    onUpdate: (ReporteHigieneData) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionTitle(
            titulo = "Paso 3: Higiene y Confort Personal",
            subtitulo = "Control de aseo corporal, vestimenta y cuidados dérmicos."
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                CheckboxItem(
                    label = "Baño completo realizado (cama o ducha)",
                    checked = data.banoRealizado,
                    onCheckedChange = { onUpdate(data.copy(banoRealizado = it)) }
                )
                CheckboxItem(
                    label = "Cambio de muda de ropa limpia",
                    checked = data.cambioRopa,
                    onCheckedChange = { onUpdate(data.copy(cambioRopa = it)) }
                )
            }
        }

        Text(
            text = "Cantidad de Cambios de Pañal / Asistencia: ${data.cambioPanalesVeces}",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0, 1, 2, 3, 4, 5).forEach { veces ->
                FilterChip(
                    selected = data.cambioPanalesVeces == veces,
                    onClick = { onUpdate(data.copy(cambioPanalesVeces = veces)) },
                    label = { Text("$veces") }
                )
            }
        }

        OutlinedTextField(
            value = data.cuidadoPiel,
            onValueChange = { onUpdate(data.copy(cuidadoPiel = it)) },
            label = { Text("Cuidado de Piel y Zonas de Apoyo") },
            placeholder = { Text("Ej: crema hidratante en talones y espalda...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = data.observacionesHigiene,
            onValueChange = { onUpdate(data.copy(observacionesHigiene = it)) },
            label = { Text("Observaciones de Higiene") },
            placeholder = { Text("Anotaciones adicionales...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SeccionIncidentesContent(
    data: ReporteIncidentesData,
    onUpdate: (ReporteIncidentesData) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionTitle(
            titulo = "Paso 4: Incidentes o Novedades Especiales",
            subtitulo = "Registra cualquier eventualidad ocurrida durante el turno."
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (data.huboIncidentes) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¿Ocurrió algún incidente durante el turno?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (data.huboIncidentes) "Requiere detalle y medidas" else "Turno sin eventualidades",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = data.huboIncidentes,
                    onCheckedChange = { onUpdate(data.copy(huboIncidentes = it)) }
                )
            }
        }

        if (data.huboIncidentes) {
            Text(
                text = "Tipo de Incidente",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tipos = listOf("Caída", "Desorientación", "Rechazo med.", "Otro")
                tipos.forEach { tipo ->
                    FilterChip(
                        selected = data.tipoIncidente == tipo,
                        onClick = { onUpdate(data.copy(tipoIncidente = tipo)) },
                        label = { Text(tipo) }
                    )
                }
            }

            OutlinedTextField(
                value = data.descripcionIncidente,
                onValueChange = { onUpdate(data.copy(descripcionIncidente = it)) },
                label = { Text("Descripción del Incidente") },
                placeholder = { Text("Hora aproximada y qué sucedió...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = data.medidasTomadas,
                onValueChange = { onUpdate(data.copy(medidasTomadas = it)) },
                label = { Text("Medidas Tomadas") },
                placeholder = { Text("Se avisó al médico de guardia, reposo...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "El turno transcurrió con normalidad sin novedades de riesgo.",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(titulo: String, subtitulo: String) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CheckboxItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ReporteBottomBar(
    pasoActual: ReporteDiarioPaso,
    isLoading: Boolean,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    onFinalizar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pasoActual != ReporteDiarioPaso.GENERAL) {
                OutlinedButton(onClick = onAnterior) {
                    Text("Anterior")
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = "Paso ${pasoActual.numero} de 4",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (pasoActual != ReporteDiarioPaso.INCIDENTES) {
                Button(onClick = onSiguiente) {
                    Text("Siguiente")
                }
            } else {
                Button(
                    onClick = onFinalizar,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text("Guardar Borrador")
                    }
                }
            }
        }
    }
}

