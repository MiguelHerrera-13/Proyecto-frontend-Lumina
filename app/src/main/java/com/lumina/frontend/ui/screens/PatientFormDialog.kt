package com.lumina.frontend.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFormDialog(
    onDismiss: () -> Unit,
    onAddPatient: (
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String
    ) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var nombreCompleto by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var diagnosticoFase by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var fechaError by remember { mutableStateOf<String?>(null) }

    // Selector de fecha nativo que bloquea fechas futuras (no se puede elegir mañana o después)
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
                style = MaterialTheme.typography.titleLarge
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

                // Selector mediante Calendario interactivo (no manual para evitar errores y bloquear fechas futuras)
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
                            diagnosticoFase.trim()
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
}
