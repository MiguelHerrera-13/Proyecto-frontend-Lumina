package com.lumina.frontend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.local.UserRole
import com.lumina.frontend.data.remote.dto.HistorialResponseDto
import com.lumina.frontend.data.remote.dto.MedicamentoResponseDto
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.ui.viewmodel.PatientDetailTab
import com.lumina.frontend.ui.viewmodel.PatientDetailViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    pacienteId: Long,
    onBack: () -> Unit,
    viewModel: PatientDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentRole = SessionManager.getCurrentRole() ?: UserRole.SUPER_ADMIN
    var mostrarDialogoEditar by remember { mutableStateOf(false) }

    LaunchedEffect(pacienteId) {
        viewModel.cargarDetallePaciente(pacienteId)
    }

    if (mostrarDialogoEditar && uiState.paciente != null) {
        EditarPacienteDialog(
            paciente = uiState.paciente!!,
            onDismiss = { mostrarDialogoEditar = false },
            onGuardar = { nombre, fecha, diag ->
                viewModel.actualizarDatosPaciente(pacienteId, nombre, fecha, diag) {
                    mostrarDialogoEditar = false
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ficha Médica del Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (currentRole == UserRole.SUPER_ADMIN && uiState.paciente != null) {
                        IconButton(onClick = { mostrarDialogoEditar = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Paciente",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando ficha médica...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.error ?: "Error al cargar la ficha",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarDetallePaciente(pacienteId) }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }

                uiState.paciente != null -> {
                    PatientDetailContent(
                        paciente = uiState.paciente!!,
                        selectedTab = uiState.selectedTab,
                        onSelectTab = { viewModel.selectTab(it) },
                        turnos = uiState.turnos,
                        medicamentos = uiState.medicamentos,
                        historial = uiState.historial,
                        onEditarClick = { mostrarDialogoEditar = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientDetailContent(
    paciente: PacienteResponseDto,
    selectedTab: PatientDetailTab,
    onSelectTab: (PatientDetailTab) -> Unit,
    turnos: List<TurnoResponseDto>,
    medicamentos: List<MedicamentoResponseDto>,
    historial: List<HistorialResponseDto>,
    onEditarClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Cabecera con datos clave del paciente y permiso de rol
        PatientHeader(paciente = paciente, onEditarClick = onEditarClick)

        // TabRow de Material 3 para navegar fluidamente entre secciones
        val tabs = PatientDetailTab.values()
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onSelectTab(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        when (tab) {
                            PatientDetailTab.GENERAL -> Icon(Icons.Default.Info, contentDescription = null)
                            PatientDetailTab.TURNOS -> Icon(Icons.Default.DateRange, contentDescription = null)
                            PatientDetailTab.MEDICAMENTOS -> Icon(Icons.Default.Favorite, contentDescription = null)
                            PatientDetailTab.HISTORIAL -> Icon(Icons.Default.List, contentDescription = null)
                        }
                    }
                )
            }
        }

        // Contenido de la pestaña activa con transición suave
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "TabContentAnimation",
            modifier = Modifier.fillMaxSize()
        ) { currentTab ->
            when (currentTab) {
                PatientDetailTab.GENERAL -> TabGeneralContent(paciente = paciente)
                PatientDetailTab.TURNOS -> TabTurnosContent(turnos = turnos)
                PatientDetailTab.MEDICAMENTOS -> TabMedicamentosContent(medicamentos = medicamentos)
                PatientDetailTab.HISTORIAL -> TabHistorialContent(historial = historial)
            }
        }
    }
}

@Composable
private fun PatientHeader(paciente: PacienteResponseDto, onEditarClick: () -> Unit = {}) {
    val iniciales = paciente.nombreCompleto
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "P" }

    val edadCalculada = calcularEdad(paciente.fechaNacimiento)
    val dniMock = (30_000_000L + (paciente.idPaciente * 1_234_567L) % 15_000_000L).toString()
    val medicoAsignadoMock = "Dr. Alejandro Gómez"

    val currentRole = SessionManager.getCurrentRole() ?: UserRole.SUPER_ADMIN

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = paciente.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Edad: $edadCalculada",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "DNI: $dniMock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Médico: $medicoAsignadoMock",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badge de Control de Edición Exclusiva para Super Admin
            Surface(
                color = if (currentRole == UserRole.SUPER_ADMIN) Color(0xFF7C3AED).copy(alpha = 0.15f) else Color(0xFF0284C7).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentRole == UserRole.SUPER_ADMIN)
                            "👑 Modo Super Admin: Edición Habilitada"
                        else
                            "🔒 Vista de Sólo Lectura (Edición exclusiva para Super Admin)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentRole == UserRole.SUPER_ADMIN) Color(0xFF7C3AED) else Color(0xFF0284C7)
                    )
                }
            }

            if (currentRole == UserRole.SUPER_ADMIN) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onEditarClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar Información del Paciente", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TabGeneralContent(paciente: PacienteResponseDto) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DetailItemCard(
                title = "Diagnóstico / Patología",
                value = paciente.diagnosticoFase ?: "Control clínico general",
                icon = Icons.Default.Info
            )
        }
        item {
            DetailItemCard(
                title = "Fecha de Nacimiento",
                value = paciente.fechaNacimiento,
                icon = Icons.Default.DateRange
            )
        }
        item {
            DetailItemCard(
                title = "Estado Clínico",
                value = if (paciente.activo) "Paciente Activo" else "Paciente Inactivo",
                icon = Icons.Default.CheckCircle,
                tint = if (paciente.activo) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
        item {
            DetailItemCard(
                title = "Identificador Único",
                value = "#PAC-${paciente.idPaciente}",
                icon = Icons.Default.Person
            )
        }
    }
}

@Composable
private fun TabTurnosContent(turnos: List<TurnoResponseDto>) {
    if (turnos.isEmpty()) {
        EmptyTabPlaceholder(message = "No hay turnos registrados para este paciente.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(turnos) { turno ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fecha compacta y resumida en lugar del string crudo
                            Text(
                                text = formatFechaCompacta(turno.fechaHora),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatusBadge(turno.estado ?: "PENDIENTE")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = turno.motivo ?: "Consulta de rutina",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Profesional: ${turno.nombreMedico ?: "Asignado"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabMedicamentosContent(medicamentos: List<MedicamentoResponseDto>) {
    if (medicamentos.isEmpty()) {
        EmptyTabPlaceholder(message = "No hay medicamentos recetados actualmente.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(medicamentos) { med ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = med.nombreMedicamento,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dosis: ${med.dosis ?: "-"} • Frecuencia: ${med.frecuencia ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (med.via != null) {
                                Text(
                                    text = "Vía: ${med.via}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        StatusBadge(if (med.activo) "ACTIVO" else "SUSPENDIDO")
                    }
                }
            }
        }
    }
}

@Composable
private fun TabHistorialContent(historial: List<HistorialResponseDto>) {
    if (historial.isEmpty()) {
        EmptyTabPlaceholder(message = "No hay registros en el historial médico.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(historial) { reg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reg.fecha,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(reg.tipo)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = reg.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (reg.profesional != null) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Registrado por: ${reg.profesional}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItemCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "CONFIRMADO", "ACTIVO", "COMPLETADO" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "PENDIENTE", "CONSULTA" -> Pair(Color(0xFFFFF3E0), Color(0xFFEF6C00))
        "INTERNACION", "SUSPENDIDO" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color(0xFFEDE7F6), Color(0xFF512DA8))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyTabPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatFechaCompacta(raw: String): String {
    return try {
        val clean = raw.replace("T", " ")
        val parts = clean.split(" ")
        val datePart = parts[0]
        val timePart = if (parts.size > 1) parts[1].take(5) else "09:00"
        val date = LocalDate.parse(datePart)
        val dayOfWeek = when (date.dayOfWeek.value) {
            1 -> "Lun"
            2 -> "Mar"
            3 -> "Mié"
            4 -> "Jue"
            5 -> "Vie"
            6 -> "Sáb"
            7 -> "Dom"
            else -> ""
        }
        val month = when (date.monthValue) {
            1 -> "Ene"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Abr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Ago"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dic"
            else -> ""
        }
        "$dayOfWeek ${date.dayOfMonth} $month • $timePart hs"
    } catch (e: Exception) {
        raw.replace("T", " ").take(16)
    }
}

private fun calcularEdad(fechaNacimiento: String): String {
    return try {
        val anioNac = fechaNacimiento.split("-")[0].toIntOrNull()
        if (anioNac != null) {
            val edad = 2026 - anioNac
            "$edad años"
        } else {
            "N/D"
        }
    } catch (e: Exception) {
        "N/D"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditarPacienteDialog(
    paciente: PacienteResponseDto,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, fechaNacimiento: String, diagnostico: String) -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf(paciente.nombreCompleto) }
    var fechaNacimiento by remember { mutableStateOf(paciente.fechaNacimiento) }
    var diagnostico by remember { mutableStateOf(paciente.diagnosticoFase ?: "") }

    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                fechaNacimiento = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            },
            calendar.get(Calendar.YEAR) - 30,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar Datos del Paciente",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = fechaNacimiento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de Nacimiento") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Elegir fecha",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { datePickerDialog.show() }
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    label = { Text("Diagnóstico Clínico") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && fechaNacimiento.isNotBlank()) {
                        onGuardar(nombre.trim(), fechaNacimiento.trim(), diagnostico.trim())
                    }
                },
                shape = RoundedCornerShape(10.dp),
                enabled = nombre.isNotBlank() && fechaNacimiento.isNotBlank()
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}