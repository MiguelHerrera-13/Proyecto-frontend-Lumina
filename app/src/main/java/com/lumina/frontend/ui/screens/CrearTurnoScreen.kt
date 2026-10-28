package com.lumina.frontend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.ui.viewmodel.CrearTurnoUiState
import com.lumina.frontend.ui.viewmodel.CrearTurnoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pantalla para solicitar o agendar un nuevo turno médico (CrearTurnoScreen).
 *
 * Cumple con los criterios de la Issue Frontend #21 (Refs #29):
 * 1. Selectores integrados de Fecha (DatePicker) y Hora (TimePicker) de Material 3.
 * 2. Dropdowns para elegir paciente y profesional asignado.
 * 3. Validación de que la hora de inicio sea anterior a la de finalización.
 * 4. Envío a POST /api/turnos con manejo amigable de errores de solapamiento (HTTP 409 Conflict).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTurnoScreen(
    onNavigateBack: () -> Unit,
    onTurnoCreado: () -> Unit = onNavigateBack,
    viewModel: CrearTurnoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val pacientes by viewModel.pacientes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados de visibilidad para los diálogos de DatePicker y TimePicker de Material 3
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePickerInicio by remember { mutableStateOf(false) }
    var mostrarTimePickerFin by remember { mutableStateOf(false) }

    // Control de desplegables (Dropdowns)
    var expandedPaciente by remember { mutableStateOf(false) }
    var expandedProfesional by remember { mutableStateOf(false) }

    // Formateadores de fecha y hora
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    }
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agendar Nuevo Turno",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // ========================================================
            // 1. BANNER VISUAL DE ERROR DE SOLAPAMIENTO (409 CONFLICT)
            // ========================================================
            AnimatedVisibility(
                visible = uiState is CrearTurnoUiState.ConflictError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (uiState is CrearTurnoUiState.ConflictError) {
                    val conflictState = uiState as CrearTurnoUiState.ConflictError
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.5.dp, Color(0xFFF87171)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "Conflicto 409",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Horario no disponible (Conflicto 409)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = conflictState.mensaje,
                                    fontSize = 13.sp,
                                    color = Color(0xFF7F1D1D),
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "💡 ${conflictState.sugerencia}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            }

            // Error genérico si ocurriera
            AnimatedVisibility(
                visible = uiState is CrearTurnoUiState.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (uiState is CrearTurnoUiState.Error) {
                    val errorState = uiState as CrearTurnoUiState.Error
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ ${errorState.mensaje}",
                            color = Color(0xFF92400E),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            // ========================================================
            // 2. SELECCIÓN DE PACIENTE (DROPDOWN)
            // ========================================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Paciente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedPaciente,
                        onExpandedChange = { expandedPaciente = !expandedPaciente },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = formState.pacienteSeleccionado?.nombreCompleto
                                ?: "Seleccionar paciente...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPaciente)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedPaciente,
                            onDismissRequest = { expandedPaciente = false }
                        ) {
                            pacientes.forEach { paciente ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = paciente.nombreCompleto,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "DNI: ${paciente.dni ?: "S/D"} • ID #${paciente.idPaciente}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.onPacienteSelected(paciente)
                                        expandedPaciente = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ========================================================
            // 3. SELECCIÓN DE PROFESIONAL MÉDICO (DROPDOWN)
            // ========================================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Profesional Asignado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedProfesional,
                        onExpandedChange = { expandedProfesional = !expandedProfesional },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = formState.profesionalSeleccionado.ifEmpty { "Seleccionar profesional..." },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProfesional)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedProfesional,
                            onDismissRequest = { expandedProfesional = false }
                        ) {
                            viewModel.profesionalesDisponibles.forEach { prof ->
                                DropdownMenuItem(
                                    text = { Text(text = prof) },
                                    onClick = {
                                        viewModel.onProfesionalSelected(prof)
                                        expandedProfesional = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ========================================================
            // 4. SELECTOR DE FECHA (DatePicker Material 3)
            // ========================================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Fecha del Turno",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedCard(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { mostrarDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Calendario",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Fecha seleccionada",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = formState.fecha.format(dateFormatter)
                                            .replaceFirstChar { it.uppercase() },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                            Text(
                                text = "Cambiar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // ========================================================
            // 5. RANGO HORARIO (TimePicker Material 3) Y VALIDACIÓN
            // ========================================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Rango Horario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tarjeta Hora Inicio
                        OutlinedCard(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (!formState.isHoraValida) Color(0xFFEF4444) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { mostrarTimePickerInicio = true }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Hora inicio",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Inicio", fontSize = 12.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formState.horaInicio.format(timeFormatter),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!formState.isHoraValida) Color(0xFFDC2626) else Color(0xFF0F172A)
                                )
                            }
                        }

                        // Tarjeta Hora Fin
                        OutlinedCard(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (!formState.isHoraValida) Color(0xFFEF4444) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { mostrarTimePickerFin = true }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Hora fin",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Fin", fontSize = 12.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formState.horaFin.format(timeFormatter),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!formState.isHoraValida) Color(0xFFDC2626) else Color(0xFF0F172A)
                                )
                            }
                        }
                    }

                    // VALIDACIÓN DE HORA (CRITERIO OBLIGATORIO: horaInicio < horaFin)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (!formState.isHoraValida) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "❌", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formState.mensajeErrorHora
                                        ?: "La hora de inicio debe ser anterior a la de finalización.",
                                    color = Color(0xFFDC2626),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        val duracionMin = java.time.Duration.between(formState.horaInicio, formState.horaFin).toMinutes()
                        Text(
                            text = "⏱️ Duración estimada del turno: $duracionMin minutos",
                            fontSize = 12.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ========================================================
            // 6. MOTIVO DE LA CONSULTA
            // ========================================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5. Motivo de la Consulta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.motivo,
                        onValueChange = { viewModel.onMotivoChanged(it) },
                        placeholder = {
                            Text("Ej: Control clínico de rutina, toma de signos vitales o consulta preventiva.")
                        },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ========================================================
            // 7. BOTÓN PRINCIPAL DE AGENDAMIENTO
            // ========================================================
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { viewModel.agendarTurno() },
                enabled = formState.isFormularioValido && uiState !is CrearTurnoUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    disabledContainerColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (uiState is CrearTurnoUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Validando y agendando...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = "Agendar Turno",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // ========================================================
    // DIÁLOGO DATEPICKER MATERIAL 3
    // ========================================================
    if (mostrarDatePicker) {
        val initialEpochMillis = remember(formState.fecha) {
            formState.fecha.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEpochMillis)

        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            viewModel.onFechaSelected(localDate)
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ========================================================
    // DIÁLOGOS TIMEPICKER MATERIAL 3 (INICIO Y FIN)
    // ========================================================
    if (mostrarTimePickerInicio) {
        val timePickerState = rememberTimePickerState(
            initialHour = formState.horaInicio.hour,
            initialMinute = formState.horaInicio.minute,
            is24Hour = true
        )

        TimePickerDialogCustom(
            title = "Hora de Inicio",
            onDismiss = { mostrarTimePickerInicio = false },
            onConfirm = {
                val hora = LocalTime.of(timePickerState.hour, timePickerState.minute)
                viewModel.onHoraInicioSelected(hora)
                mostrarTimePickerInicio = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (mostrarTimePickerFin) {
        val timePickerState = rememberTimePickerState(
            initialHour = formState.horaFin.hour,
            initialMinute = formState.horaFin.minute,
            is24Hour = true
        )

        TimePickerDialogCustom(
            title = "Hora de Finalización",
            onDismiss = { mostrarTimePickerFin = false },
            onConfirm = {
                val hora = LocalTime.of(timePickerState.hour, timePickerState.minute)
                viewModel.onHoraFinSelected(hora)
                mostrarTimePickerFin = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    // ========================================================
    // DIÁLOGO DE ÉXITO AL COMPLETAR LA CREACIÓN DEL TURNO
    // ========================================================
    if (uiState is CrearTurnoUiState.Success) {
        val nuevoTurno = (uiState as CrearTurnoUiState.Success).turno
        AlertDialog(
            onDismissRequest = onTurnoCreado,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Turno Agendado con Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "El turno fue registrado correctamente en el sistema.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "👤 Paciente: ${formState.pacienteSeleccionado?.nombreCompleto}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "🩺 Profesional: ${nuevoTurno.nombreMedico}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "📅 Fecha y Hora: ${nuevoTurno.fechaHora}",
                        color = Color(0xFF0284C7),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onTurnoCreado,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Finalizar y Volver")
                }
            }
        )
    }
}

/**
 * Envoltorio de Diálogo Material 3 para el TimePicker interactivo.
 */
@Composable
private fun TimePickerDialogCustom(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                content()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}
