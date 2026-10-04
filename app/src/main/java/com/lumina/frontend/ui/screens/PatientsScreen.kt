package com.lumina.frontend.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.frontend.ui.viewmodel.PatientsViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsScreen(
    onBack: () -> Unit,
    patientsViewModel: PatientsViewModel = viewModel()
) {

    var showPatientForm by remember {
        mutableStateOf(false)
    }
    var patientId by remember {
        mutableStateOf("")
    }
    val uiState = patientsViewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pacientes")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    patientsViewModel.limpiarEstado()
                    showPatientForm = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar paciente")
            }
            OutlinedTextField(
                value = patientId,
                onValueChange = {
                    patientId = it
                },
                label = {
                    Text("ID del paciente")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val id = patientId.toLongOrNull()

                    if (id != null) {
                        patientsViewModel.obtenerPacientePorId(id)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar paciente")
            }
            if (uiState.isLoading) {
                CircularProgressIndicator()

                Text(
                    text = "Guardando paciente..."
                )
            }

            uiState.errorMessage?.let { mensaje ->
                Text(
                    text = "Error: $mensaje",
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.successMessage?.let { mensaje ->
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            uiState.patient?.let { paciente ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = paciente.nombreCompleto,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Fecha de nacimiento: ${paciente.fechaNacimiento}"
                        )

                        Text(
                            text = "Diagnóstico: ${
                                paciente.diagnosticoFase ?: "Sin especificar"
                            }"
                        )

                        Text(
                            text = if (paciente.activo) {
                                "Estado: Activo"
                            } else {
                                "Estado: Inactivo"
                            }
                        )
                    }
                }
            }
        }
    }

    if (showPatientForm) {
        PatientFormDialog(
            onDismiss = {
                showPatientForm = false
            },

            onAddPatient = { nombre, fecha, diagnostico ->

                showPatientForm = false

                patientsViewModel.crearPaciente(
                    nombreCompleto = nombre,
                    fechaNacimiento = fecha,
                    diagnosticoFase = diagnostico.ifBlank {
                        null
                    }
                )
            }
        )
    }
}