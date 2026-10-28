package com.lumina.frontend.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.repository.TurnoRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

enum class AccionTurno(val titulo: String, val descripcion: String) {
    CANCELAR("Cancelar Turno", "Indica el motivo de la cancelación. Esta acción es definitiva."),
    REPROGRAMAR("Reprogramar Turno", "Selecciona una nueva fecha, horario y el motivo del cambio.")
}

/**
 * Diálogo modal para cancelar o reprogramar un turno médico.
 *
 * Criterios de Aceptación:
 * 1. Campo de texto para justificar la cancelación o reprogramación (mínimo 10 caracteres obligatorios).
 * 2. En reprogramación, selectores integrados de nueva fecha y hora.
 * 3. Conexión a endpoints POST /api/turnos/{id}/cancelar y reprogramar.
 * 4. Desacoplamiento total: la vista principal solo pasa el ID y la acción; el diálogo
 *    se encarga de sus propias validaciones y llamadas de red.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnoAccionDialog(
    turnoId: Long,
    accionInicial: AccionTurno,
    onDismiss: () -> Unit,
    onSuccess: (mensaje: String) -> Unit,
    modifier: Modifier = Modifier,
    repository: TurnoRepository = remember { TurnoRepository() }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var accionActual by remember { mutableStateOf(accionInicial) }
    var motivo by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var horaSeleccionada by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Validación desacoplada interna
    val caracteresMinimos = 10
    val motivoValido = motivo.trim().length >= caracteresMinimos
    val fechaHoraValida = if (accionActual == AccionTurno.REPROGRAMAR) {
        fechaSeleccionada.isNotBlank() && horaSeleccionada.isNotBlank()
    } else {
        true
    }
    val formularioValido = motivoValido && fechaHoraValida && !isSubmitting

    // Calendario para los pickers nativos
    val calendario = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaSeleccionada = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                horaSeleccionada = String.format(Locale.US, "%02d:%02d", hourOfDay, minute)
            },
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        )
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) onDismiss()
        },
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (accionActual == AccionTurno.CANCELAR) Color(0xFFFEE2E2) else Color(0xFFE0F2FE),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (accionActual == AccionTurno.CANCELAR) "❌" else "🗓️",
                            fontSize = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = accionActual.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (accionActual == AccionTurno.CANCELAR) Color(0xFF991B1B) else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Turno #$turnoId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Selector de modo (Cancelar vs Reprogramar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AccionTurno.values().forEach { accion ->
                        val isSelected = accionActual == accion
                        FilterChip(
                            selected = isSelected,
                            onClick = { accionActual = accion },
                            label = { Text(accion.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (accion == AccionTurno.CANCELAR) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = if (accion == AccionTurno.CANCELAR) Color(0xFF991B1B) else MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = accionActual.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // SELECTORES DE FECHA Y HORA (Visibles únicamente en Reprogramación)
                if (accionActual == AccionTurno.REPROGRAMAR) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Nueva Fecha y Horario",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Selector de Fecha
                        OutlinedTextField(
                            value = if (fechaSeleccionada.isBlank()) "Toca para elegir fecha" else fechaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha reprogramada") },
                            trailingIcon = {
                                Text(
                                    text = "📅",
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable { datePickerDialog.show() }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() }
                        )

                        // Selector de Hora
                        OutlinedTextField(
                            value = if (horaSeleccionada.isBlank()) "Toca para elegir hora" else horaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora reprogramada") },
                            trailingIcon = {
                                Text(
                                    text = "⏰",
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable { timePickerDialog.show() }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { timePickerDialog.show() }
                        )
                    }
                }

                // CAMPO OBLIGATORIO DE MOTIVO (Mínimo 10 caracteres)
                Column {
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = {
                            motivo = it
                            errorMessage = null
                        },
                        label = { Text("Motivo obligatorio") },
                        placeholder = {
                            Text(
                                if (accionActual == AccionTurno.CANCELAR)
                                    "Ej: Paciente hospitalizado por urgencia médica..."
                                else
                                    "Ej: Solicitud de cambio de horario por consulta..."
                            )
                        },
                        minLines = 3,
                        maxLines = 4,
                        isError = motivo.isNotEmpty() && !motivoValido,
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = when {
                                        motivo.isEmpty() -> "Mínimo 10 caracteres obligatorios"
                                        !motivoValido -> "Faltan ${caracteresMinimos - motivo.trim().length} caracteres"
                                        else -> "✓ Motivo válido"
                                    },
                                    color = if (motivoValido) Color(0xFF059669) else if (motivo.isEmpty()) Color.Gray else MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${motivo.trim().length}/$caracteresMinimos",
                                    fontSize = 11.sp,
                                    color = if (motivoValido) Color(0xFF059669) else Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Mensaje de error general si falló la llamada
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!formularioValido) return@Button
                    isSubmitting = true
                    errorMessage = null

                    coroutineScope.launch {
                        val resultado = if (accionActual == AccionTurno.CANCELAR) {
                            repository.cancelarTurno(turnoId, motivo.trim())
                        } else {
                            val nuevaFechaHora = "$fechaSeleccionada $horaSeleccionada"
                            repository.reprogramarTurno(turnoId, motivo.trim(), nuevaFechaHora)
                        }

                        isSubmitting = false

                        resultado.fold(
                            onSuccess = {
                                val mensaje = if (accionActual == AccionTurno.CANCELAR) {
                                    "Turno #$turnoId cancelado correctamente."
                                } else {
                                    "Turno #$turnoId reprogramado para $fechaSeleccionada $horaSeleccionada."
                                }
                                onSuccess(mensaje)
                                onDismiss()
                            },
                            onFailure = { ex ->
                                errorMessage = "Error: ${ex.message ?: "No se pudo procesar la solicitud."}"
                            }
                        )
                    }
                },
                enabled = formularioValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (accionActual == AccionTurno.CANCELAR) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enviando...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (accionActual == AccionTurno.CANCELAR) "Confirmar Cancelación" else "Confirmar Reprogramación"
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cerrar")
            }
        }
    )
}
