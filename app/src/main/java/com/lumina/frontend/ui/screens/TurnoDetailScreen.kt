package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

/**
 * Pantalla completa de Detalle de Turno (TurnoDetailScreen).
 *
 * Cumple con el criterio de Issue #33:
 * - Botón "Añadir a mi calendario" en la vista de detalle del turno.
 * - Lanzamiento del Intent con CalendarContract.Events.CONTENT_URI precargando título, fechas y descripción.
 * - Manejo seguro de intents de calendario en Android.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnoDetailScreen(
    turno: TurnoResponseDto,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onCancelarTurno: (() -> Unit)? = null,
    onReprogramarTurno: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val isCancelado = turno.estado == "CANCELADO"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Turno",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TARJETA PRINCIPAL DEL TURNO
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = turno.nombreMedico ?: "Dr. Asignado",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Especialidad Médica / Cuidados",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        // Badge de estado
                        val badgeBg = when (turno.estado) {
                            "CANCELADO" -> Color(0xFFFEE2E2)
                            "REPROGRAMADO" -> Color(0xFFE0E7FF)
                            "COMPLETADO" -> Color(0xFFD1FAE5)
                            else -> Color(0xFFFEF3C7)
                        }
                        val badgeText = when (turno.estado) {
                            "CANCELADO" -> Color(0xFF991B1B)
                            "REPROGRAMADO" -> Color(0xFF3730A3)
                            "COMPLETADO" -> Color(0xFF065F46)
                            else -> Color(0xFF92400E)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeBg)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = turno.estado ?: "PROGRAMADO",
                                color = badgeText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(18.dp))

                    // Horario y fecha
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE0F2FE),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "📅", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Fecha y Hora Programada",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = turno.fechaHora,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Motivo
                    Row(verticalAlignment = Alignment.Top) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "📋", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Motivo de la Consulta",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = turno.motivo ?: "Control de rutina y seguimiento de plan de cuidados.",
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            // BOTÓN PRINCIPAL REQUERIDO: AÑADIR A MI CALENDARIO
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFFF0F9FF)
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Sincronización con tu Dispositivo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0369A1)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Guarda este turno en tu Google Calendar o app de calendario nativa con recordatorio automático.",
                        fontSize = 13.sp,
                        color = Color(0xFF0C4A6E),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            CalendarExportHelper.exportarTurnoAlCalendario(
                                context = context,
                                turno = turno,
                                onFeedback = { exito, mensaje ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(mensaje)
                                    }
                                }
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(text = "🗓️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Añadir a mi calendario",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ACCIONES SECUNDARIAS (REPROGRAMAR / CANCELAR)
            if (!isCancelado && (onReprogramarTurno != null || onCancelarTurno != null)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    onReprogramarTurno?.let { onReprog ->
                        OutlinedButton(
                            onClick = onReprog,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reprogramar")
                        }
                    }

                    onCancelarTurno?.let { onCanc ->
                        TextButton(
                            onClick = onCanc,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar Turno")
                        }
                    }
                }
            }
        }
    }
}
