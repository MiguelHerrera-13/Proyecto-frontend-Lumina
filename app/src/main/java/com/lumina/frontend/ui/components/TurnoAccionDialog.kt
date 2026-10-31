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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.local.UserRole
import androidx.compose.material.icons.filled.Mail
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

enum class AccionTurno(val titulo: String, val descripcion: String) {
    CANCELAR("Cancelar Turno", "Indica el motivo de la cancelación. Esta acción es definitiva."),
    REPROGRAMAR("Reprogramar Turno", "Selecciona una nueva fecha, horario y el motivo del cambio.")
}

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
    var mostrarConfirmacionEmail by remember { mutableStateOf(false) }

    val caracteresMinimos = 10
    val motivoValido = motivo.trim().length >= caracteresMinimos
    val fechaHoraValida = if (accionActual == AccionTurno.REPROGRAMAR) {
        fechaSeleccionada.isNotBlank() && horaSeleccionada.isNotBlank()
    } else {
        true
    }
    val formularioValido = motivoValido && fechaHoraValida && !isSubmitting

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
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (accionActual == AccionTurno.CANCELAR) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (accionActual == AccionTurno.CANCELAR) Icons.Default.Cancel else Icons.Default.EditCalendar,
                            contentDescription = null,
                            tint = if (accionActual == AccionTurno.CANCELAR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = accionActual.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (accionActual == AccionTurno.CANCELAR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
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
                                selectedContainerColor = if (accion == AccionTurno.CANCELAR) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = if (accion == AccionTurno.CANCELAR) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
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

                // SELECTORES DE FECHA Y HORA (Visibles únicamente en Reprogramación) con diseño unificado
                if (accionActual == AccionTurno.REPROGRAMAR) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Nueva Fecha y Horario Programado",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Selector de Fecha
                        OutlinedTextField(
                            value = if (fechaSeleccionada.isBlank()) "Toca para elegir fecha" else fechaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha reprogramada") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Elegir fecha",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { datePickerDialog.show() }
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(10.dp),
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
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Elegir hora",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { timePickerDialog.show() }
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(10.dp),
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
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
                                val role = SessionManager.getCurrentRole()
                                if (role == UserRole.USUARIO) {
                                    mostrarConfirmacionEmail = true
                                } else {
                                    val mensaje = if (accionActual == AccionTurno.CANCELAR) {
                                        "Turno #$turnoId cancelado correctamente."
                                    } else {
                                        "Turno #$turnoId reprogramado para $fechaSeleccionada $horaSeleccionada."
                                    }
                                    onSuccess(mensaje)
                                    onDismiss()
                                }
                            },
                            onFailure = { ex ->
                                errorMessage = "Error: ${ex.message ?: "No se pudo procesar la solicitud."}"
                            }
                        )
                    }
                },
                enabled = formularioValido,
                shape = RoundedCornerShape(10.dp),
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

    if (mostrarConfirmacionEmail) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionEmail = false
                onSuccess("Solicitud de $turnoId enviada")
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Confirmación por Correo Electrónico",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Tu solicitud de ${if (accionActual == AccionTurno.CANCELAR) "cancelación" else "reprogramación"} para el turno #$turnoId ha sido procesada con éxito.\n\nRecibirás la confirmación final en tu correo electrónico registrado y ha sido notificada a lumina.salud.adm@gmail.com.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacionEmail = false
                        onSuccess("Solicitud de $turnoId procesada")
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}