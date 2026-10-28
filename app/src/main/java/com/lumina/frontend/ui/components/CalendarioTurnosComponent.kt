package com.lumina.frontend.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.remote.dto.TurnoResponseDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Modo de navegación del calendario interactivo: Semanal o Mensual.
 */
enum class ModoCalendario {
    SEMANAL,
    MENSUAL
}

/**
 * Modelo inmutable representativo de un día en el calendario.
 */
data class DiaCalendario(
    val fecha: LocalDate,
    val fechaIso: String,
    val numeroDia: Int,
    val nombreDia: String,
    val esHoy: Boolean,
    val tieneTurnos: Boolean,
    val cantidadTurnos: Int = 0
)

/**
 * Componente interactivo de calendario semanal / mensual para visualizar y seleccionar
 * turnos del día, cumpliendo con los criterios de la Issue Frontend #20 (Refs #28):
 *
 * 1. Fila horizontal con selector de días de la semana y visualización del mes actual.
 * 2. Indicador con punto de color bajo los días que tienen turnos programados.
 * 3. Al seleccionar un día, actualización reactiva de la lista de turnos asociados en pantalla.
 *
 * Optimizado contra recomposiciones innecesarias mediante granularidad de composables (DiaCalendarioCard),
 * memorización de colecciones con 'remember(turnos)' y claves estables en LazyRow.
 */
@Composable
fun CalendarioTurnosComponent(
    turnos: List<TurnoResponseDto>,
    fechaSeleccionadaIso: String?,
    onFechaSeleccionada: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoy = remember { LocalDate.now() }
    var modoCalendario by remember { mutableStateOf(ModoCalendario.SEMANAL) }

    // Fechas base para la paginación de semana y mes
    var fechaBaseSemana by remember { mutableStateOf(hoy) }
    var mesBase by remember { mutableStateOf(YearMonth.from(hoy)) }

    // Memorización del mapeo de fechas con turnos para evitar recalcularlo al cambiar de día
    val fechasConTurnos = remember(turnos) {
        val map = mutableMapOf<String, Int>()
        turnos.forEach { turno ->
            extraerFechaIso(turno.fechaHora)?.let { iso ->
                map[iso] = (map[iso] ?: 0) + 1
            }
        }
        map
    }

    // Lista de fechas a renderizar según el modo actual (Semanal o Mensual)
    val fechas = remember(modoCalendario, fechaBaseSemana, mesBase) {
        when (modoCalendario) {
            ModoCalendario.SEMANAL -> {
                val lunes = fechaBaseSemana.with(DayOfWeek.MONDAY)
                (0L..6L).map { lunes.plusDays(it) }
            }
            ModoCalendario.MENSUAL -> {
                (1..mesBase.lengthOfMonth()).map { mesBase.atDay(it) }
            }
        }
    }

    // Título de cabecera con el mes y año actual
    val tituloMesAno = remember(modoCalendario, fechaBaseSemana, mesBase) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
        when (modoCalendario) {
            ModoCalendario.SEMANAL -> fechaBaseSemana.format(formatter)
            ModoCalendario.MENSUAL -> mesBase.format(formatter)
        }.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }
    }

    // Transformación estable a modelos inmutables DiaCalendario
    val diasCalendario = remember(fechas, fechasConTurnos, hoy) {
        fechas.map { fecha ->
            val iso = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val cant = fechasConTurnos[iso] ?: 0
            val nombre = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                .replace(".", "")
                .uppercase()
            DiaCalendario(
                fecha = fecha,
                fechaIso = iso,
                numeroDia = fecha.dayOfMonth,
                nombreDia = nombre,
                esHoy = (fecha == hoy),
                tieneTurnos = (cant > 0),
                cantidadTurnos = cant
            )
        }
    }

    val lazyListState = rememberLazyListState()

    // Scroll automático suave hacia el día seleccionado o hacia 'Hoy'
    LaunchedEffect(diasCalendario, fechaSeleccionadaIso) {
        val targetIndex = if (!fechaSeleccionadaIso.isNullOrBlank()) {
            diasCalendario.indexOfFirst { it.fechaIso == fechaSeleccionadaIso }
        } else {
            diasCalendario.indexOfFirst { it.esHoy }
        }
        if (targetIndex >= 0) {
            lazyListState.animateScrollToItem(targetIndex)
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // CABECERA: TÍTULO DEL MES, NAVEGACIÓN Y SELECTOR DE VISTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📅", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tituloMesAno,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Text(
                        text = if (modoCalendario == ModoCalendario.SEMANAL) "Vista Semanal" else "Vista Mensual",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Botón para retroceder semana o mes
                    IconButton(
                        onClick = {
                            if (modoCalendario == ModoCalendario.SEMANAL) {
                                fechaBaseSemana = fechaBaseSemana.minusWeeks(1)
                            } else {
                                mesBase = mesBase.minusMonths(1)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Anterior",
                            tint = Color(0xFF334155)
                        )
                    }

                    // Botón "Hoy"
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                fechaBaseSemana = hoy
                                mesBase = YearMonth.from(hoy)
                                val hoyIso = hoy.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                onFechaSeleccionada(hoyIso)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Ir a hoy",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hoy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Botón para avanzar semana o mes
                    IconButton(
                        onClick = {
                            if (modoCalendario == ModoCalendario.SEMANAL) {
                                fechaBaseSemana = fechaBaseSemana.plusWeeks(1)
                            } else {
                                mesBase = mesBase.plusMonths(1)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Siguiente",
                            tint = Color(0xFF334155)
                        )
                    }

                    // Selector de Modo: Semanal / Mensual
                    FilterChip(
                        selected = (modoCalendario == ModoCalendario.MENSUAL),
                        onClick = {
                            modoCalendario = if (modoCalendario == ModoCalendario.SEMANAL) {
                                ModoCalendario.MENSUAL
                            } else {
                                ModoCalendario.SEMANAL
                            }
                        },
                        label = {
                            Text(
                                text = if (modoCalendario == ModoCalendario.SEMANAL) "Mes" else "Semana",
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // FILA HORIZONTAL DE SELECTOR DE DÍAS (LazyRow optimizado)
            LazyRow(
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = diasCalendario,
                    key = { it.fechaIso }
                ) { dia ->
                    DiaCalendarioCard(
                        dia = dia,
                        isSelected = (dia.fechaIso == fechaSeleccionadaIso),
                        onClick = {
                            // Si se vuelve a tocar el mismo día seleccionado, se deselecciona para ver todos
                            if (dia.fechaIso == fechaSeleccionadaIso) {
                                onFechaSeleccionada(null)
                            } else {
                                onFechaSeleccionada(dia.fechaIso)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BARRA DE ESTADO DEL FILTRO SELECCIONADO
            AnimatedVisibility(
                visible = !fechaSeleccionadaIso.isNullOrBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                fechaSeleccionadaIso?.let { iso ->
                    val cantidad = fechasConTurnos[iso] ?: 0
                    val fechaFormateada = remember(iso) { formatearFechaParaLectura(iso) }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0F9FF),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = fechaFormateada,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                                Text(
                                    text = when (cantidad) {
                                        0 -> "Sin turnos programados"
                                        1 -> "1 turno programado"
                                        else -> "$cantidad turnos programados"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFF0284C7)
                                )
                            }

                            TextButton(
                                onClick = { onFechaSeleccionada(null) },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar filtro de fecha",
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF0284C7)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ver todos",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta atómica e inmutable para cada celda de día en la fila horizontal.
 *
 * El desacoplamiento en este composable granular garantiza que Compose salte
 * la recomposición de los días no afectados cuando cambia la fecha seleccionada.
 */
@Composable
private fun DiaCalendarioCard(
    dia: DiaCalendario,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> Color(0xFF0284C7) // Color primario activo
        dia.esHoy -> Color(0xFFF0F9FF)
        else -> Color(0xFFFAFAFA)
    }

    val borderColor = when {
        isSelected -> Color(0xFF0284C7)
        dia.esHoy -> Color(0xFF38BDF8)
        else -> Color(0xFFE2E8F0)
    }

    val textoNombreColor = when {
        isSelected -> Color(0xFFE0F2FE)
        dia.esHoy -> Color(0xFF0284C7)
        else -> Color(0xFF64748B)
    }

    val textoNumeroColor = when {
        isSelected -> Color.White
        dia.esHoy -> Color(0xFF0369A1)
        else -> Color(0xFF0F172A)
    }

    // Indicador con punto de color bajo los días que tienen turnos programados
    val dotColor = when {
        isSelected -> Color.White
        dia.tieneTurnos -> Color(0xFF0284C7)
        else -> Color.Transparent
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(if (dia.esHoy || isSelected) 1.5.dp else 1.dp, borderColor),
        modifier = modifier
            .width(58.dp)
            .height(82.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Nombre del día abreviado (LUN, MAR, MIÉ, etc.)
            Text(
                text = dia.nombreDia,
                fontSize = 11.sp,
                fontWeight = if (dia.esHoy || isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textoNombreColor,
                textAlign = TextAlign.Center
            )

            // Número del día del mes
            Text(
                text = "${dia.numeroDia}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textoNumeroColor,
                textAlign = TextAlign.Center
            )

            // Indicador con punto de color bajo los días con turnos programados
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

/**
 * Función auxiliar para extraer de manera segura la fecha en formato ISO ("yyyy-MM-dd")
 * a partir de cadenas como "2026-10-28T10:00:00" o "2026-10-28 10:00".
 */
fun extraerFechaIso(fechaHoraStr: String?): String? {
    if (fechaHoraStr.isNullOrBlank()) return null
    val trimmed = fechaHoraStr.trim()
    return if (trimmed.length >= 10 && trimmed[4] == '-' && trimmed[7] == '-') {
        trimmed.substring(0, 10)
    } else {
        null
    }
}

/**
 * Formatea una fecha ISO para presentación en lenguaje natural amigable en español.
 */
fun formatearFechaParaLectura(fechaIso: String): String {
    return try {
        val date = LocalDate.parse(fechaIso)
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        date.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }
    } catch (_: Exception) {
        fechaIso
    }
}
