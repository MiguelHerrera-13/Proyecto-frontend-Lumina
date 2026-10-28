package com.lumina.frontend.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.model.TurnoFiltros
import java.util.Calendar
import java.util.Locale

/**
 * Modal Bottom Sheet para aplicar filtros avanzados a la lista de turnos médicos.
 *
 * Criterios de Aceptación (Issue #32 / Frontend #24):
 * 1. Despliegue mediante ModalBottomSheet desde la barra superior.
 * 2. Controles interactivos para:
 *    - Filtrado por estado múltiple (PROGRAMADO, PENDIENTE, COMPLETADO, CANCELADO, REPROGRAMADO).
 *    - Filtrado por profesional médico.
 *    - Filtrado por rango de fechas (Fecha Desde y Fecha Hasta) con DatePickerDialog nativo.
 * 3. Botones "Limpiar Filtros" y "Aplicar" con recarga reactiva de la lista.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TurnoFiltrosBottomSheet(
    filtrosActuales: TurnoFiltros,
    onDismissRequest: () -> Unit,
    onAplicarFiltros: (TurnoFiltros) -> Unit,
    onLimpiarFiltros: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current

    // Estado local para edición antes de presionar "Aplicar"
    var estadosTemp by remember { mutableStateOf(filtrosActuales.estadosSeleccionados) }
    var profesionalTemp by remember { mutableStateOf(filtrosActuales.profesionalQuery) }
    var fechaDesdeTemp by remember { mutableStateOf(filtrosActuales.fechaDesde) }
    var fechaHastaTemp by remember { mutableStateOf(filtrosActuales.fechaHasta) }

    val calendario = remember { Calendar.getInstance() }

    val datePickerDesde = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaDesdeTemp = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
    }

    val datePickerHasta = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaHastaTemp = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // CABECERA DEL BOTTOM SHEET
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🔍", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Filtros Avanzados",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Filtra turnos por estado, médico y fechas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar filtros"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // SECCIÓN 1: ESTADO MÚLTIPLE
            Text(
                text = "Estado del Turno (Selección múltiple)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecciona uno o más estados para acotar los resultados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TurnoFiltros.ESTADOS_DISPONIBLES.forEach { estado ->
                    val isSelected = estadosTemp.contains(estado)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            estadosTemp = if (isSelected) {
                                estadosTemp - estado
                            } else {
                                estadosTemp + estado
                            }
                        },
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
                                text = estado.lowercase().replaceFirstChar { it.uppercase() },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (estado == "CANCELADO") Color(0xFFFEE2E2) else MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = if (estado == "CANCELADO") Color(0xFF991B1B) else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN 2: PROFESIONAL MÉDICO
            Text(
                text = "Profesional Médico",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = profesionalTemp,
                onValueChange = { profesionalTemp = it },
                label = { Text("Buscar por nombre del médico") },
                placeholder = { Text("Ej: Dr. Gómez o Dra. Martínez") },
                singleLine = true,
                trailingIcon = {
                    if (profesionalTemp.isNotBlank()) {
                        IconButton(onClick = { profesionalTemp = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar profesional")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN 3: RANGO DE FECHAS (DESDE / HASTA)
            Text(
                text = "Rango de Fechas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selector Fecha Desde
                OutlinedTextField(
                    value = fechaDesdeTemp ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Desde") },
                    placeholder = { Text("AAAA-MM-DD") },
                    leadingIcon = {
                        Text(text = "📅", fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
                    },
                    trailingIcon = {
                        if (fechaDesdeTemp != null) {
                            IconButton(onClick = { fechaDesdeTemp = null }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Borrar fecha desde", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { datePickerDesde.show() }
                )

                // Selector Fecha Hasta
                OutlinedTextField(
                    value = fechaHastaTemp ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hasta") },
                    placeholder = { Text("AAAA-MM-DD") },
                    leadingIcon = {
                        Text(text = "📅", fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
                    },
                    trailingIcon = {
                        if (fechaHastaTemp != null) {
                            IconButton(onClick = { fechaHastaTemp = null }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Borrar fecha hasta", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { datePickerHasta.show() }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(20.dp))

            // BOTONES DE ACCIÓN: LIMPIAR FILTROS Y APLICAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        estadosTemp = emptySet()
                        profesionalTemp = ""
                        fechaDesdeTemp = null
                        fechaHastaTemp = null
                        onLimpiarFiltros()
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Limpiar Filtros",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = {
                        val filtrosNuevos = TurnoFiltros(
                            estadosSeleccionados = estadosTemp,
                            profesionalQuery = profesionalTemp,
                            fechaDesde = fechaDesdeTemp,
                            fechaHasta = fechaHastaTemp
                        )
                        onAplicarFiltros(filtrosNuevos)
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Aplicar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
