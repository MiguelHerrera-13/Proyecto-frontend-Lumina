package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    onPatientClick: (Long) -> Unit = {},
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
            FloatingActionButton(
                onClick = {
                    patientsViewModel.limpiarEstado()
                    showPatientForm = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Paciente"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Barra de Búsqueda Reactiva
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { patientsViewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre, ID o diagnóstico...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { patientsViewModel.limpiarBusqueda() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Chips de Filtro por Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PatientStatusFilter.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { patientsViewModel.onFilterSelected(filter) },
                        label = {
                            Text(
                                text = filter.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Contador de resultados
            if (!uiState.isLoading && uiState.errorMessage == null) {
                val total = uiState.filteredPatients.size
                Text(
                    text = "$total paciente(s) encontrado(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Mensajes informativos
            uiState.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            uiState.successMessage?.let { success ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = success,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Estado de Carga
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredPatients.isEmpty() && uiState.errorMessage == null) {
                // Estado Vacío
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
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
                // Lista de Pacientes Reactiva
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = uiState.filteredPatients,
                        key = { it.idPaciente }
                    ) { paciente ->
                        PatientCardItem(
                            paciente = paciente,
                            onClick = { onPatientClick(paciente.idPaciente) }
                        )
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
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
