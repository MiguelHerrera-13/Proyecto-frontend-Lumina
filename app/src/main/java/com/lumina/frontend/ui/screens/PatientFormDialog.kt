package com.lumina.frontend.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.remote.RetrofitClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlinx.coroutines.launch

data class ProfesionalOption(
    val id: Long?,
    val nombre: String,
    val rol: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFormDialog(
    onDismiss: () -> Unit,
    onAddPatient: (
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String,
        idEmpleadoAsignado: Long?,
        nombreEmpleadoAsignado: String?,
        rolEmpleadoAsignado: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val calendar = remember { Calendar.getInstance() }

    var nombreCompleto by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var diagnosticoFase by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var fechaError by remember { mutableStateOf<String?>(null) }

    // Lista de profesionales médicos y empleados disponibles
    val defaultProfesionales = listOf(
        ProfesionalOption(null, "Sin profesional asignado", "NINGUNO"),
        ProfesionalOption(39L, "Leonel Salomón", "MEDICO"),
        ProfesionalOption(40L, "Miguel Herrera", "CUIDADOR"),
        ProfesionalOption(41L, "Nicolás Penna", "MEDICO"),
        ProfesionalOption(30L, "Dr. Cuidador", "EMPLEADO"),
        ProfesionalOption(32L, "Dr. Carlos Prueba", "MEDICO")
    )
    var profesionalesDisponibles by remember { mutableStateOf(defaultProfesionales) }
    var profesionalSeleccionado by remember { mutableStateOf(defaultProfesionales[1]) } // Por defecto Leonel Salomón o médico
    var mostrarDialogoSeleccionProfesional by remember { mutableStateOf(false) }

    // Cargar profesionales en tiempo real desde la base de datos MySQL
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val resp = RetrofitClient.usuarioApiService.obtenerUsuarios("EMPLEADOS_Y_MEDICOS")
                if (resp.isSuccessful && resp.body() != null) {
                    val usuariosDb = resp.body()!!
                    val mapeados = usuariosDb.map { u ->
                        ProfesionalOption(
                            id = u.idUsuario,
                            nombre = u.nombre,
                            rol = u.rol ?: "EMPLEADO"
                        )
                    }
                    if (mapeados.isNotEmpty()) {
                        val combinada = mutableListOf(ProfesionalOption(null, "Sin profesional asignado", "NINGUNO"))
                        combinada.addAll(mapeados)
                        profesionalesDisponibles = combinada
                        profesionalSeleccionado = combinada.firstOrNull { it.id != null } ?: combinada.first()
                    }
                }
            } catch (e: Exception) {
                // Mantener lista por defecto
            }
        }
    }

    // Selector de fecha nativo que bloquea fechas futuras
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                fechaNacimiento = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                fechaError = null
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
                text = "Registrar Nuevo Paciente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = nombreCompleto,
                    onValueChange = {
                        nombreCompleto = it
                        nombreError = null
                    },
                    label = { Text("Nombre y Apellido") },
                    placeholder = { Text("Ej: Rosa Benítez") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    isError = nombreError != null,
                    supportingText = {
                        nombreError?.let { Text(it) }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Selector mediante Calendario interactivo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = if (fechaNacimiento.isBlank()) "Toca para abrir calendario" else fechaNacimiento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de nacimiento") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendario",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = fechaError != null,
                        supportingText = {
                            fechaError?.let { Text(it) }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { datePickerDialog.show() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = diagnosticoFase,
                    onValueChange = { diagnosticoFase = it },
                    label = { Text("Diagnóstico clínico y patología") },
                    placeholder = { Text("Ej: Alzheimer (Fase 1 - Inicial)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de Médico o Empleado Asignado
                Text(
                    text = "Asignar Médico o Empleado a cargo:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    onClick = { mostrarDialogoSeleccionProfesional = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = profesionalSeleccionado.nombre,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (profesionalSeleccionado.id != null) "Rol: ${profesionalSeleccionado.rol}" else "Opcional",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Desplegar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (nombreCompleto.isBlank()) {
                        nombreError = "El nombre es obligatorio"
                        hasError = true
                    }
                    if (fechaNacimiento.isBlank()) {
                        fechaError = "Debes seleccionar la fecha en el calendario"
                        hasError = true
                    }

                    if (!hasError) {
                        onAddPatient(
                            nombreCompleto.trim(),
                            fechaNacimiento,
                            diagnosticoFase.trim(),
                            profesionalSeleccionado.id,
                            if (profesionalSeleccionado.id != null) profesionalSeleccionado.nombre else null,
                            if (profesionalSeleccionado.id != null) profesionalSeleccionado.rol else null
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // Diálogo Modal para seleccionar al Profesional médico o empleado
    if (mostrarDialogoSeleccionProfesional) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSeleccionProfesional = false },
            title = {
                Text(
                    text = "Seleccionar Profesional a Cargo",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(profesionalesDisponibles) { opcion ->
                        val isSelected = (opcion.id == profesionalSeleccionado.id && opcion.nombre == profesionalSeleccionado.nombre)
                        Surface(
                            onClick = {
                                profesionalSeleccionado = opcion
                                mostrarDialogoSeleccionProfesional = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = opcion.nombre,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = if (opcion.id != null) "Rol: ${opcion.rol}" else "Sin asignación directa",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoSeleccionProfesional = false }) {
                    Text("Listo")
                }
            }
        )
    }
}
