package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.data.remote.dto.PacienteResponseDto
import com.lumina.frontend.ui.viewmodel.PatientStatusFilter
import com.lumina.frontend.ui.viewmodel.PatientsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsScreen(
    onBack: () -> Unit,
    patientsViewModel: PatientsViewModel = viewModel()
) {
    val uiState by patientsViewModel.uiState.collectAsState()
    val searchQuery by patientsViewModel.searchQuery.collectAsState()
    val selectedFilter by patientsViewModel.selectedFilter.collectAsState()

    var showPatientForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Directorio de Pacientes",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { patientsViewModel.cargarPacientes() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    patientsViewModel.limpiarEstado()
                    showPatientForm = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo") },
                text = { Text("Nuevo Paciente") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Barra de búsqueda reactiva
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { patientsViewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Buscar por nombre, apellido o ID...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { patientsViewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Chips de filtro por estado (Material 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == PatientStatusFilter.ALL,
                    onClick = { patientsViewModel.onFilterSelected(PatientStatusFilter.ALL) },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = selectedFilter == PatientStatusFilter.ACTIVE,
                    onClick = { patientsViewModel.onFilterSelected(PatientStatusFilter.ACTIVE) },
                    label = { Text("Activos") }
                )
                FilterChip(
                    selected = selectedFilter == PatientStatusFilter.INACTIVE,
                    onClick = { patientsViewModel.onFilterSelected(PatientStatusFilter.INACTIVE) },
                    label = { Text("Inactivos") }
                )
            }

            // Contador de resultados y estado de carga
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.filteredPatients.size} pacientes encontrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            // Mensajes de error o éxito
            uiState.errorMessage?.let { errorMsg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            uiState.successMessage?.let { successMsg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE6F4EA)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = successMsg,
                        color = Color(0xFF137333),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Lista de pacientes o Estado Vacío
            if (uiState.filteredPatients.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "No se encontraron pacientes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Prueba modificando el término de búsqueda o el filtro de estado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = uiState.filteredPatients,
                        key = { it.idPaciente }
                    ) { paciente ->
                        PatientCardItem(paciente = paciente)
                    }
                }
            }
        }
    }

    if (showPatientForm) {
        PatientFormDialog(
            onDismiss = { showPatientForm = false },
            onAddPatient = { nombre, fecha, diagnostico ->
                showPatientForm = false
                patientsViewModel.crearPaciente(
                    nombreCompleto = nombre,
                    fechaNacimiento = fecha,
                    diagnosticoFase = diagnostico.ifBlank { null }
                )
            }
        )
    }
}

@Composable
fun PatientCardItem(
    paciente: PacienteResponseDto,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con inicial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val initial = paciente.nombreCompleto.firstOrNull()?.uppercaseChar()?.toString() ?: "P"
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Información principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = paciente.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge de Estado Activo / Inactivo
                    StatusBadge(activo = paciente.activo)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ID: #${paciente.idPaciente}  •  Nacimiento: ${paciente.fechaNacimiento}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                val diagText = paciente.diagnosticoFase ?: "Sin especificar"
                Text(
                    text = "Diagnóstico: $diagText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    activo: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (activo) Color(0xFFE6F4EA) else Color(0xFFF1F3F4)
    val textColor = if (activo) Color(0xFF137333) else Color(0xFF5F6368)
    val text = if (activo) "Activo" else "Inactivo"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}