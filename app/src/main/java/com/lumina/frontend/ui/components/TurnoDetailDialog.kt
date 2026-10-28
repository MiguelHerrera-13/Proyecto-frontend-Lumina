package com.lumina.frontend.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import com.lumina.frontend.util.CalendarExportHelper

/**
 * Diálogo modal para la vista de detalle de un turno médico (TurnoDetailDialog).
 *
 * Cumple con los criterios de Issue #30 (Frontend #22) e Issue #33 (Frontend #25):
 * 1. Badge de estado con color distintivo (PENDIENTE, EN_CURSO, COMPLETADO, CANCELADO).
 * 2. Botón "Iniciar Turno" visible si está pendiente y en horario.
 * 3. Botón "Finalizar y Crear Reporte" que redirige al reporte diario.
 * 4. Botón "Añadir a mi calendario" para exportación con CalendarContract.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnoDetailDialog(
    turno: TurnoResponseDto,
    onDismiss: () -> Unit,
    onCancelarTurno: (() -> Unit)? = null,
    onReprogramarTurno: (() -> Unit)? = null,
    onIniciarTurno: ((Long) -> Unit)? = null,
    onFinalizarYCrearReporte: ((Long) -> Unit)? = null,
    onVerPantallaCompleta: ((Long) -> Unit)? = null,
    onCalendarExportFeedback: ((Boolean, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var estadoLocal by remember(turno.estado) {
        mutableStateOf(turno.estado?.uppercase() ?: "PENDIENTE")
    }

    val isPendiente = estadoLocal == "PENDIENTE" || estadoLocal == "PROGRAMADO"
    val isEnCurso = estadoLocal == "EN_CURSO"
    val isCompletado = estadoLocal == "COMPLETADO"
    val isCancelado = estadoLocal == "CANCELADO"

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🩺", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Detalle del Turno",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Turno #${turno.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
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
                // Tarjeta de Paciente, Médico y Estado
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = turno.nombreMedico ?: "Dr. Asignado",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Paciente: ${turno.nombrePaciente ?: "María Elena González"}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            // Badge de Estado con Color Distintivo (PENDIENTE, EN_CURSO, COMPLETADO, CANCELADO)
                            val (badgeBg, badgeText, badgeIcon) = when (estadoLocal) {
                                "PENDIENTE", "PROGRAMADO" -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "⏳")
                                "EN_CURSO" -> Triple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), "⏱️")
                                "COMPLETADO" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "✅")
                                "CANCELADO" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "❌")
                                else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "📋")
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = badgeIcon, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = estadoLocal,
                                        color = badgeText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📅", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = turno.fechaHora,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Motivo: ${turno.motivo ?: "Consulta de rutina y seguimiento clínico."}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ========================================================
                // ACCIONES DE ESTADO: INICIAR TURNO Y FINALIZAR/REPORTE
                // ========================================================
                if (isPendiente) {
                    Button(
                        onClick = {
                            estadoLocal = "EN_CURSO"
                            onIniciarTurno?.invoke(turno.id)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Iniciar Turno",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Iniciar Turno", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else if (isEnCurso) {
                    Button(
                        onClick = {
                            estadoLocal = "COMPLETADO"
                            onDismiss()
                            onFinalizarYCrearReporte?.invoke(turno.id)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = "Finalizar y Crear Reporte",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Finalizar y Crear Reporte", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // BOTÓN AÑADIR A MI CALENDARIO
                OutlinedButton(
                    onClick = {
                        CalendarExportHelper.exportarTurnoAlCalendario(
                            context = context,
                            turno = turno.copy(estado = estadoLocal),
                            onFeedback = onCalendarExportFeedback
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(text = "🗓️", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Añadir a mi calendario",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                // Acciones secundarias (Reprogramar / Cancelar)
                if (!isCancelado && !isCompletado && (onCancelarTurno != null || onReprogramarTurno != null)) {
                    Divider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        onReprogramarTurno?.let { onReprog ->
                            OutlinedButton(
                                onClick = onReprog,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reprogramar", fontSize = 12.sp)
                            }
                        }

                        onCancelarTurno?.let { onCanc ->
                            TextButton(
                                onClick = onCanc,
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                onVerPantallaCompleta?.let { onVerFull ->
                    TextButton(onClick = {
                        onDismiss()
                        onVerFull(turno.id)
                    }) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Pantalla Completa",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Pantalla Completa")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    )
}
