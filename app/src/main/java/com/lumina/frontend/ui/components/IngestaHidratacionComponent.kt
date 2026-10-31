package com.lumina.frontend.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.model.IngestaHidratacionData
import com.lumina.frontend.data.model.TipoComida

/**
 * Componente modular para el registro visual de ingesta de alimentos y control de hidratación.
 * Diseñado para ser ágil, intuitivo y utilizable por cuidadores en turnos asistenciales.
 * Desacoplado para insertarse directamente en pantallas individuales o en formularios con Stepper.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IngestaHidratacionComponent(
    data: IngestaHidratacionData,
    onTipoComidaChange: (TipoComida) -> Unit,
    onPorcentajeChange: (Int) -> Unit,
    onIncrementarVasos: () -> Unit,
    onDecrementarVasos: () -> Unit,
    onToggleEtiqueta: (String) -> Unit,
    onNotasChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCampoNotas by remember { mutableStateOf(data.notasEspeciales.isNotBlank()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TARJETA 1: REGISTRO DE INGESTA DE COMIDA
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Cabecera de la sección
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "\uD83C\uDF7D\uFE0F",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Registro de Ingesta",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Comida seleccionada y porcentaje ingerido",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Badge del porcentaje actual
                    Surface(
                        color = getPorcentajeColor(data.porcentajeIngesta).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${data.porcentajeIngesta}%",
                            color = getPorcentajeColor(data.porcentajeIngesta),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de tipo de comida (Chips rápidos)
                Text(
                    text = "Momento del día",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoComida.values().forEach { tipo ->
                        val isSelected = data.tipoComida == tipo
                        FilterChip(
                            selected = isSelected,
                            onClick = { onTipoComidaChange(tipo) },
                            label = {
                                Text(
                                    text = tipo.displayName,
                                    fontSize = 12.sp,
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

                Spacer(modifier = Modifier.height(14.dp))

                // Selector visual de porcentaje (0%, 25%, 50%, 75%, 100%)
                Text(
                    text = "Porcentaje consumido de la porción",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IngestaHidratacionData.PORCENTAJES_DISPONIBLES.forEach { pct ->
                        val isSelected = data.porcentajeIngesta == pct
                        val targetColor = if (isSelected) getPorcentajeColor(pct) else MaterialTheme.colorScheme.surfaceVariant
                        val animatedColor by animateColorAsState(targetColor, label = "chipColor")

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(animatedColor)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) getPorcentajeColor(pct) else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onPorcentajeChange(pct) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$pct%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barra de progreso y descripción del estado
                val progressAnimated by animateFloatAsState(
                    targetValue = data.porcentajeIngesta / 100f,
                    label = "progress"
                )
                LinearProgressIndicator(
                    progress = progressAnimated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = getPorcentajeColor(data.porcentajeIngesta),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = getPorcentajeDescripcion(data.porcentajeIngesta),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }

        // TARJETA 2: CONTROL DE HIDRATACIÓN (VASOS DE 250 ML)
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Cabecera Hidratación
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "\uD83D\uDCA7",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Control de Hidratación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Contador rápido (250 ml por vaso)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Total acumulado automático
                    Surface(
                        color = Color(0xFF0284C7).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${data.totalMililitrosAgua} ml",
                            color = Color(0xFF0369A1),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Contador principal de vasos con botones rápidos + y -
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F9FF))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Restar Vaso
                    FilledTonalIconButton(
                        onClick = onDecrementarVasos,
                        enabled = data.vasosAgua > 0,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text(
                            text = "−",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (data.vasosAgua > 0) Color(0xFF0284C7) else Color.Gray
                        )
                    }

                    // Contador visual central
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${data.vasosAgua}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0C4A6E)
                            )
                            Text(
                                text = if (data.vasosAgua == 1) " vaso" else " vasos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0284C7),
                                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                            )
                        }

                        Text(
                            text = "Equivalente a ${String.format(java.util.Locale.US, "%.2f", data.totalLitrosAgua)} Litros",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF0369A1)
                        )
                    }

                    // Botón Sumar Vaso (+250 ml)
                    FilledTonalIconButton(
                        onClick = onIncrementarVasos,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar vaso (+250 ml)",
                            tint = Color(0xFF0284C7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila visual de representación de vasos (hasta 8 vasos)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..8) {
                        val isFilled = i <= data.vasosAgua
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(width = 24.dp, height = 30.dp)
                                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topStart = 2.dp, topEnd = 2.dp))
                                .background(if (isFilled) Color(0xFF38BDF8) else Color(0xFFE2E8F0))
                                .border(
                                    1.dp,
                                    if (isFilled) Color(0xFF0284C7) else Color(0xFFCBD5E1),
                                    RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topStart = 2.dp, topEnd = 2.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isFilled) {
                                Text(
                                    text = "💧",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                if (data.vasosAgua > 8) {
                    Text(
                        text = "+${data.vasosAgua - 8} vasos adicionales",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF0369A1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // TARJETA 3: NOTAS ESPECIALES Y OBSERVACIONES CLÍNICAS (APETITO / DEGLUCIÓN)
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "\uD83D\uDCDD",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Observaciones y Apetito",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Apetito, dificultad de deglución o alertas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Botón para desplegar / ocultar notas personalizadas
                    FilledTonalButton(
                        onClick = { mostrarCampoNotas = !mostrarCampoNotas }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar nota",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (mostrarCampoNotas) "Ocultar" else "Nota",
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Etiquetas rápidas de un solo toque (clave para cuidadores con poco tiempo)
                Text(
                    text = "Etiquetas rápidas (un toque)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IngestaHidratacionData.ETIQUETAS_COMUNES.forEach { etiqueta ->
                        val isSelected = data.etiquetasRapidas.contains(etiqueta)
                        val isDisfagia = etiqueta.contains("Deglución", ignoreCase = true) || etiqueta.contains("Disfagia", ignoreCase = true)

                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleEtiqueta(etiqueta) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            label = {
                                Text(
                                    text = etiqueta,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDisfagia) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = if (isDisfagia) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                // Campo de texto expandible para notas adicionales
                if (mostrarCampoNotas) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = data.notasEspeciales,
                        onValueChange = onNotasChange,
                        label = { Text("Detalle de observaciones clínicas") },
                        placeholder = { Text("Ej: Mostró dificultad para tragar sólidos, se administró papilla...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

// Helpers visuales para colores según porcentaje
private fun getPorcentajeColor(porcentaje: Int): Color {
    return when (porcentaje) {
        0 -> Color(0xFFEF4444)     // Rojo (Inapetencia / alerta)
        25 -> Color(0xFFF97316)    // Naranja (Muy bajo)
        50 -> Color(0xFFF59E0B)    // Ámbar (Medio)
        75 -> Color(0xFF84CC16)    // Lima (Bueno)
        100 -> Color(0xFF10B981)   // Verde Esmeralda (Completo)
        else -> Color(0xFF6B7280)
    }
}

private fun getPorcentajeDescripcion(porcentaje: Int): String {
    return when (porcentaje) {
        0 -> "0% - Nada consumido (Rechazo total)"
        25 -> "25% - Ingesta mínima (Aproximadamente 1/4)"
        50 -> "50% - Ingesta regular (La mitad)"
        75 -> "75% - Ingesta buena (Casi todo)"
        100 -> "100% - Porción completa finalizada"
        else -> "$porcentaje% consumido"
    }
}
