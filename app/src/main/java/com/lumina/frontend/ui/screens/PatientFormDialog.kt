package com.lumina.frontend.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp

@Composable
fun PatientFormDialog(
    onDismiss: () -> Unit,
    onAddPatient: (
        nombreCompleto: String,
        fechaNacimiento: String,
        diagnosticoFase: String
    ) -> Unit
) {

    var nombreCompleto by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var diagnosticoFase by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var fechaError by remember { mutableStateOf<String?>(null) }

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
                    value = nombreCompleto,
                    onValueChange = {
                        nombreCompleto = it
                        nombreError = null
                    },
                    label = {
                        Text("Nombre completo")
                    },
                    isError = nombreError != null,
                    supportingText = {
                        nombreError?.let {
                            Text(it)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = {
                        fechaNacimiento = it
                        fechaError = null
                    },
                    label = {
                        Text("Fecha de nacimiento")
                    },
                    placeholder = {
                        Text("2000-05-20")
                    },
                    isError = fechaError != null,
                    supportingText = {
                        fechaError?.let {
                            Text(it)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = diagnosticoFase,
                    onValueChange = {
                        diagnosticoFase = it
                    },
                    label = {
                        Text("Diagnóstico / fase")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {

                    nombreError =
                        if (nombreCompleto.isBlank()) {
                            "El nombre es obligatorio"
                        } else {
                            null
                        }

                    val formatoFecha =
                        Regex("""\d{4}-\d{2}-\d{2}""")

                    fechaError = when {
                        fechaNacimiento.isBlank() ->
                            "La fecha de nacimiento es obligatoria"

                        !formatoFecha.matches(fechaNacimiento.trim()) ->
                            "Usá el formato AAAA-MM-DD"

                        else -> null
                    }

                    if (
                        nombreError == null &&
                        fechaError == null
                    ) {
                        onAddPatient(
                            nombreCompleto.trim(),
                            fechaNacimiento.trim(),
                            diagnosticoFase.trim()
                        )
                    }
                }
            ) {
                Text("Agregar")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}