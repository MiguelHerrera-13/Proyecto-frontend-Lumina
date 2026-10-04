package com.lumina.frontend.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lumina.frontend.data.model.Patient

@Composable
fun PatientFormDialog(
    nextId: Int,
    onDismiss: () -> Unit,
    onAddPatient: (Patient) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var roomError by remember { mutableStateOf<String?>(null) }
    var conditionError by remember { mutableStateOf<String?>(null) }
    var statusError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Agregar paciente")
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Nombre completo") },
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        age = it
                        ageError = null
                    },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = ageError != null,
                    supportingText = {
                        ageError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = {
                        room = it
                        roomError = null
                    },
                    label = { Text("Habitación") },
                    isError = roomError != null,
                    supportingText = {
                        roomError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = condition,
                    onValueChange = {
                        condition = it
                        conditionError = null
                    },
                    label = { Text("Condición") },
                    isError = conditionError != null,
                    supportingText = {
                        conditionError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = status,
                    onValueChange = {
                        status = it
                        statusError = null
                    },
                    label = { Text("Estado") },
                    isError = statusError != null,
                    supportingText = {
                        statusError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAge = age.toIntOrNull()

                    nameError =
                        if (name.isBlank()) "El nombre es obligatorio" else null

                    ageError = when {
                        age.isBlank() -> "La edad es obligatoria"
                        parsedAge == null || parsedAge <= 0 ->
                            "Ingresá una edad válida"
                        else -> null
                    }

                    roomError =
                        if (room.isBlank()) "La habitación es obligatoria" else null

                    conditionError =
                        if (condition.isBlank()) "La condición es obligatoria" else null

                    statusError =
                        if (status.isBlank()) "El estado es obligatorio" else null

                    val isValid =
                        nameError == null &&
                                ageError == null &&
                                roomError == null &&
                                conditionError == null &&
                                statusError == null

                    if (isValid && parsedAge != null) {
                        onAddPatient(
                            Patient(
                                id = nextId,
                                name = name.trim(),
                                age = parsedAge,
                                room = room.trim(),
                                condition = condition.trim(),
                                status = status.trim()
                            )
                        )
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}