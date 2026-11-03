package com.lumina.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.local.UserRole
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class VisitaFamiliarItem(
    val paciente: String,
    val especialidad: String,
    val horario: String,
    val motivo: String,
    val fecha: String = "Mañana"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDashboardScreen(
    onNavigateToPatients: () -> Unit,
    onNavigateToTurnosCalendar: () -> Unit,
    onNavigateToCrearTurno: () -> Unit,
    onNavigateToIngesta: () -> Unit,
    onNavigateToCrearReporte: (Long) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by SessionManager.currentSession.collectAsState()
    val role = session?.rol ?: UserRole.SUPER_ADMIN
    val roleColor = Color(role.colorHex)

    // Diálogo para solicitud de visita médica en rol USUARIO / Familiar
    var mostrarDialogoVisita by remember { mutableStateOf(false) }
    var visitaEnviadaExito by remember { mutableStateOf(false) }
    var visitaSolicitadaItem by remember { mutableStateOf<VisitaFamiliarItem?>(null) }

    var dialogPacienteNombre by remember { mutableStateOf("") }
    var dialogEspecialidad by remember { mutableStateOf("Medicina General") }
    var dialogHorario by remember { mutableStateOf("Mañana (09:30 - 10:30 hs)") }
    var dialogMotivo by remember { mutableStateOf("") }

    if (mostrarDialogoVisita) {
        val defaultNombre = if (dialogPacienteNombre.isNotBlank()) dialogPacienteNombre else "Familiar de ${session?.nombre ?: "Usuario"}"

        AlertDialog(
            onDismissRequest = { mostrarDialogoVisita = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Solicitar Visita Médica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Notificación a: cuidadores.ies@gmail.com",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Se coordinará una visita médica individual de 1 hora de atención para tu familiar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = if (dialogPacienteNombre.isBlank()) defaultNombre else dialogPacienteNombre,
                        onValueChange = { dialogPacienteNombre = it },
                        label = { Text("Nombre del Paciente / Familiar") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dialogEspecialidad,
                        onValueChange = { dialogEspecialidad = it },
                        label = { Text("Especialidad requerida") },
                        placeholder = { Text("Ej: Medicina General, Kinesiología...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dialogHorario,
                        onValueChange = { dialogHorario = it },
                        label = { Text("Horario sugerido (1h a 1.5h de atención)") },
                        placeholder = { Text("Ej: Mañana (09:30 - 10:30 hs)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dialogMotivo,
                        onValueChange = { dialogMotivo = it },
                        label = { Text("Motivo de la visita") },
                        placeholder = { Text("Ej: Control semestral de presión y marcha...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pacNombre = if (dialogPacienteNombre.isNotBlank()) dialogPacienteNombre.trim() else defaultNombre
                        val mot = if (dialogMotivo.isNotBlank()) dialogMotivo.trim() else "Evaluación médica integral"
                        visitaSolicitadaItem = VisitaFamiliarItem(
                            paciente = pacNombre,
                            especialidad = dialogEspecialidad,
                            horario = dialogHorario,
                            motivo = mot,
                            fecha = "Mañana"
                        )
                        mostrarDialogoVisita = false
                        visitaEnviadaExito = true
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Enviar Solicitud a Gmail")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoVisita = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (visitaEnviadaExito) {
        AlertDialog(
            onDismissRequest = { visitaEnviadaExito = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("¡Solicitud Transmitida!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Tu solicitud ha sido transmitida exitosamente al correo de guardia médica (cuidadores.ies@gmail.com). El turno ya se refleja en tu pantalla inicial en espera de confirmación.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { visitaEnviadaExito = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(roleColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = role.icono,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lúmina",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Salud y Cuidado Integral",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                SessionManager.logout()
                                onLogout()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ==========================================
            // HEADER HERO DE BIENVENIDA (ALINEADO Y ESTÉTICO)
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        roleColor.copy(alpha = 0.10f),
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(roleColor.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = role.icono, fontSize = 22.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Bienvenido/a",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = session?.nombre ?: "Usuario",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = roleColor.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = role.titulo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = roleColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = role.subtitulo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // ==========================================
            // CONTENIDO ESPECÍFICO SEGÚN EL ROL
            // ==========================================
            when (role) {
                UserRole.SUPER_ADMIN -> {
                    item {
                        SuperAdminSection(
                            onNavigateToPatients = onNavigateToPatients,
                            onNavigateToTurnosCalendar = onNavigateToTurnosCalendar,
                            onNavigateToCrearTurno = onNavigateToCrearTurno,
                            onNavigateToIngesta = onNavigateToIngesta,
                            onNavigateToCrearReporte = { onNavigateToCrearReporte(101L) }
                        )
                    }
                }
                UserRole.EMPLEADO -> {
                    item {
                        EmpleadoSection(
                            onNavigateToPatients = onNavigateToPatients,
                            onNavigateToTurnosCalendar = onNavigateToTurnosCalendar,
                            onNavigateToCrearTurno = onNavigateToCrearTurno,
                            onNavigateToIngesta = onNavigateToIngesta,
                            onNavigateToCrearReporte = { onNavigateToCrearReporte(101L) },
                            isDemo = (session?.email == "cuidador@lumina.com"),
                            nombreEmpleado = session?.nombre ?: "Profesional de Salud"
                        )
                    }
                }
                UserRole.USUARIO -> {
                    item {
                        UsuarioSection(
                            onNavigateToTurnosCalendar = onNavigateToTurnosCalendar,
                            onNavigateToPatients = onNavigateToPatients,
                            onSolicitarVisitaClick = { mostrarDialogoVisita = true },
                            isDemo = (session?.email == "familiar@lumina.com"),
                            nombreUsuario = session?.nombre ?: "Usuario",
                            visitaSolicitada = visitaSolicitadaItem
                        )
                    }
                }
            }
        }
    }
}

// =======================================================================
// SECCIÓN 1: VISTA DE SUPER ADMINISTRADOR (MÉTRICAS UNIFORMES Y MÓDULOS COMPACTOS)
// =======================================================================
@Composable
private fun SuperAdminSection(
    onNavigateToPatients: () -> Unit,
    onNavigateToTurnosCalendar: () -> Unit,
    onNavigateToCrearTurno: () -> Unit,
    onNavigateToIngesta: () -> Unit,
    onNavigateToCrearReporte: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Métricas Globales Clínicas (Cajas con tamaño idéntico garantizado de 118.dp)
        Text(
            text = "Métricas Generales de la Clínica",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                titulo = "Pacientes",
                valor = "24",
                subtitulo = "Activos",
                icono = Icons.Default.Person,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                titulo = "Turnos Hoy",
                valor = "8",
                subtitulo = "Confirmados",
                icono = Icons.Default.CalendarMonth,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                titulo = "Reportes",
                valor = "15",
                subtitulo = "Últimas 24h",
                icono = Icons.Default.Assignment,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Módulos de Gestión y Operaciones",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Tarjetas compactas, uniformes y del mismo tamaño (72.dp de altura)
        ActionNavCard(
            titulo = "Gestión de Pacientes",
            subtitulo = "Directorio clínico, búsqueda y ficha médica",
            icono = Icons.Default.Person,
            colorIcono = MaterialTheme.colorScheme.primary,
            tag = "Paginación",
            onClick = onNavigateToPatients
        )

        ActionNavCard(
            titulo = "Agenda y Calendario de Turnos",
            subtitulo = "Vista semanal/mensual y reprogramación",
            icono = Icons.Default.CalendarMonth,
            colorIcono = MaterialTheme.colorScheme.secondary,
            tag = "Calendario",
            onClick = onNavigateToTurnosCalendar
        )

        ActionNavCard(
            titulo = "Agendar Nuevo Turno Médico",
            subtitulo = "Solicitud médica con detección de conflicto 409",
            icono = Icons.Default.Add,
            colorIcono = Color(0xFF0284C7),
            tag = "Turnos",
            onClick = onNavigateToCrearTurno
        )

        ActionNavCard(
            titulo = "Control de Ingesta & Hidratación",
            subtitulo = "Registro de comidas diarias y balance hídrico",
            icono = Icons.Default.Restaurant,
            colorIcono = MaterialTheme.colorScheme.tertiary,
            tag = "Nutrición",
            onClick = onNavigateToIngesta
        )

        ActionNavCard(
            titulo = "Reporte Diario del Turno",
            subtitulo = "Asistente guiado en 4 pasos con validación",
            icono = Icons.Default.Assignment,
            colorIcono = Color(0xFF7C3AED),
            tag = "Stepper",
            onClick = onNavigateToCrearReporte
        )
    }
}

// Modelo representativo de turnos de atención individual (1 hora a 1.5 horas máximo)
private data class TurnoAtencionEmpleado(
    val id: Long,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val paciente: String,
    val habitacion: String,
    val tarea: String,
    val duracionMinutos: Int,
    val esManana: Boolean = false
)

// =======================================================================
// SECCIÓN 2: VISTA DE EMPLEADO (HORARIOS DE 1H A 1.5H CON PASE AUTOMÁTICO)
// =======================================================================
@Composable
private fun EmpleadoSection(
    onNavigateToPatients: () -> Unit,
    onNavigateToTurnosCalendar: () -> Unit,
    onNavigateToCrearTurno: () -> Unit,
    onNavigateToIngesta: () -> Unit,
    onNavigateToCrearReporte: () -> Unit,
    isDemo: Boolean,
    nombreEmpleado: String
) {
    if (!isDemo) {
        // VISTA LIMPIA PARA EMPLEADO NUEVO / REGISTRADO (DESDE CERO)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Jornada Asistencial Inicial",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Estado: Agenda libre para comenzar",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Sin turnos hoy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🩺", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hola, $nombreEmpleado",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "No tienes turnos clínicos asignados en este momento. A medida que agendes nuevos turnos o se te asignen pacientes, aparecerán aquí con su horario de 1h a 1.5h.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCrearTurno,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Agendar Turno", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNavigateToPatients,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ver Pacientes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(
                text = "Acciones Clínicas del Turno",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ActionNavCard(
                titulo = "Agendar Nuevo Turno Médico",
                subtitulo = "Horarios asistenciales de 1h a 1.5h por paciente",
                icono = Icons.Default.Add,
                colorIcono = Color(0xFF0284C7),
                tag = "Turnos",
                onClick = onNavigateToCrearTurno
            )

            ActionNavCard(
                titulo = "Completar Reporte Diario",
                subtitulo = "Observaciones del turno, higiene y signos vitales",
                icono = Icons.Default.Assignment,
                colorIcono = Color(0xFF7C3AED),
                tag = "Stepper",
                onClick = onNavigateToCrearReporte
            )

            ActionNavCard(
                titulo = "Registrar Ingesta e Hidratación",
                subtitulo = "Porcentaje de comida consumida y vasos de agua",
                icono = Icons.Default.Restaurant,
                colorIcono = MaterialTheme.colorScheme.tertiary,
                tag = "Nutrición",
                onClick = onNavigateToIngesta
            )

            ActionNavCard(
                titulo = "Mis Turnos Programados (Hoy en adelante)",
                subtitulo = "Cronograma filtrado de 1h a 1.5h por paciente",
                icono = Icons.Default.CalendarMonth,
                colorIcono = MaterialTheme.colorScheme.secondary,
                tag = "Próximos",
                onClick = onNavigateToTurnosCalendar
            )

            ActionNavCard(
                titulo = "Ficha de Mis Pacientes Asignados",
                subtitulo = "Diagnósticos médicos, medicamentos e historial",
                icono = Icons.Default.Person,
                colorIcono = MaterialTheme.colorScheme.primary,
                tag = "Clínica",
                onClick = onNavigateToPatients
            )
        }
    } else {
        // Cronograma asistencial de turnos demo (1h a 1.5h por paciente)
        val turnosEmpleado = remember {
            listOf(
                TurnoAtencionEmpleado(
                    id = 1L,
                    horaInicio = LocalTime.of(8, 30),
                    horaFin = LocalTime.of(9, 30),
                    paciente = "Roberto Gómez",
                    habitacion = "Hab. 204",
                    tarea = "Control de medicación matutina y signos vitales",
                    duracionMinutos = 60
                ),
                TurnoAtencionEmpleado(
                    id = 2L,
                    horaInicio = LocalTime.of(10, 0),
                    horaFin = LocalTime.of(11, 30),
                    paciente = "Jorge Méndez",
                    habitacion = "Hab. 108",
                    tarea = "Movilidad asistida y estimulación motora",
                    duracionMinutos = 90
                ),
                TurnoAtencionEmpleado(
                    id = 3L,
                    horaInicio = LocalTime.of(12, 0),
                    horaFin = LocalTime.of(13, 0),
                    paciente = "Roberto Gómez",
                    habitacion = "Hab. 204",
                    tarea = "Asistencia en almuerzo y registro de ingesta",
                    duracionMinutos = 60
                ),
                TurnoAtencionEmpleado(
                    id = 4L,
                    horaInicio = LocalTime.of(14, 0),
                    horaFin = LocalTime.of(15, 30),
                    paciente = "Jorge Méndez",
                    habitacion = "Hab. 108",
                    tarea = "Terapia neurocognitiva y descanso",
                    duracionMinutos = 90
                ),
                TurnoAtencionEmpleado(
                    id = 5L,
                    horaInicio = LocalTime.of(16, 0),
                    horaFin = LocalTime.of(17, 0),
                    paciente = "Roberto Gómez",
                    habitacion = "Hab. 204",
                    tarea = "Merienda asistida, aseo y control vespertino",
                    duracionMinutos = 60
                ),
                TurnoAtencionEmpleado(
                    id = 6L,
                    horaInicio = LocalTime.of(8, 30),
                    horaFin = LocalTime.of(10, 0),
                    paciente = "Roberto Gómez",
                    habitacion = "Hab. 204",
                    tarea = "Asistencia matutina de desayuno e higiene",
                    duracionMinutos = 90,
                    esManana = true
                ),
                TurnoAtencionEmpleado(
                    id = 7L,
                    horaInicio = LocalTime.of(10, 30),
                    horaFin = LocalTime.of(11, 30),
                    paciente = "Jorge Méndez",
                    habitacion = "Hab. 108",
                    tarea = "Control clínico matutino y signos",
                    duracionMinutos = 60,
                    esManana = true
                )
            )
        }

        val now = remember { LocalTime.now() }
        val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

        // Detección reactiva: al cumplirse el horario de atención, pasa automáticamente al siguiente paciente
        val indexNatural = remember(turnosEmpleado, now) {
            val idx = turnosEmpleado.indexOfFirst { !it.esManana && now.isBefore(it.horaFin) }
            if (idx != -1) idx else turnosEmpleado.indexOfFirst { it.esManana }
        }

        // Offset interactivo para que el usuario pueda avanzar manualmente de turno al probar en el dispositivo
        var turnoIndexOffset by remember { mutableStateOf(0) }
        val currentIndex = (indexNatural + turnoIndexOffset) % turnosEmpleado.size
        val turnoActual = turnosEmpleado[currentIndex]
        val siguienteTurno = turnosEmpleado[(currentIndex + 1) % turnosEmpleado.size]
        val isEnCurso = !turnoActual.esManana && now >= turnoActual.horaInicio && now < turnoActual.horaFin

        val duracionLabel = if (turnoActual.duracionMinutos == 60) "1 hora" else "1 hora y media"

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Tarjeta Dinámica de Atención al Paciente (1h a 1.5h con pase al siguiente paciente)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isEnCurso) "Atención en Curso" else if (turnoActual.esManana) "Próxima Atención (Mañana)" else "Próxima Atención Asignada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Duración: $duracionLabel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val badgeColor = when {
                            isEnCurso -> Color(0xFF10B981)
                            turnoActual.esManana -> MaterialTheme.colorScheme.primary
                            else -> Color(0xFF0284C7)
                        }
                        val badgeTexto = when {
                            isEnCurso -> "En Atención"
                            turnoActual.esManana -> "Mañana"
                            else -> "Próximo"
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = badgeColor
                        ) {
                            Text(
                                text = badgeTexto,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Datos del paciente y horario
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🩺", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${turnoActual.paciente} (${turnoActual.habitacion})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "⏰ ${if (turnoActual.esManana) "Mañana " else ""}${turnoActual.horaInicio.format(timeFormatter)} - ${turnoActual.horaFin.format(timeFormatter)} hs ($duracionLabel)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tarea: ${turnoActual.tarea}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila con siguiente paciente en línea y botón de avance para prueba
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Siguiente paciente en turno:",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${siguienteTurno.paciente} (${siguienteTurno.horaInicio.format(timeFormatter)} hs)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = { turnoIndexOffset++ },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Siguiente ⏭️",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Text(
                text = "Acciones Clínicas del Turno",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ActionNavCard(
                titulo = "Completar Reporte Diario",
                subtitulo = "Observaciones del turno, higiene y signos vitales",
                icono = Icons.Default.Assignment,
                colorIcono = Color(0xFF0284C7),
                tag = "Obligatorio",
                onClick = onNavigateToCrearReporte
            )

            ActionNavCard(
                titulo = "Registrar Ingesta e Hidratación",
                subtitulo = "Porcentaje de comida consumida y vasos de agua",
                icono = Icons.Default.Restaurant,
                colorIcono = MaterialTheme.colorScheme.tertiary,
                tag = "Nutrición",
                onClick = onNavigateToIngesta
            )

            ActionNavCard(
                titulo = "Mis Turnos Programados (Hoy en adelante)",
                subtitulo = "Cronograma filtrado de 1h a 1.5h por paciente",
                icono = Icons.Default.CalendarMonth,
                colorIcono = MaterialTheme.colorScheme.secondary,
                tag = "Próximos",
                onClick = onNavigateToTurnosCalendar
            )

            ActionNavCard(
                titulo = "Ficha de Mis Pacientes Asignados",
                subtitulo = "Diagnósticos médicos, medicamentos e historial",
                icono = Icons.Default.Person,
                colorIcono = MaterialTheme.colorScheme.primary,
                tag = "Clínica",
                onClick = onNavigateToPatients
            )
        }
    }
}

// =======================================================================
// SECCIÓN 3: VISTA DE USUARIO (PORTAL FAMILIAR)
// =======================================================================
@Composable
private fun UsuarioSection(
    onNavigateToTurnosCalendar: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onSolicitarVisitaClick: () -> Unit,
    isDemo: Boolean,
    nombreUsuario: String,
    visitaSolicitada: VisitaFamiliarItem?
) {
    if (!isDemo) {
        // VISTA LIMPIA PARA USUARIO / FAMILIAR REGISTRADO (DESDE CERO)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Tarjeta de Bienvenida Onboarding
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏡", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Portal Familiar Activo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Cuenta familiar vinculada a $nombreUsuario",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Bienvenido/a, $nombreUsuario",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tu portal familiar está limpio y listo para comenzar desde cero. Podés solicitar una visita médica personalizada de 1 hora de atención para que el equipo clínico atienda a tu familiar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Próxima Visita Médica (Dinámica si ya se solicitó, o limpia si no)
            if (visitaSolicitada != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Próxima Visita Médica Solicitada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "En Revisión • 1h",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${visitaSolicitada.fecha} • ${visitaSolicitada.horario}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Paciente: ${visitaSolicitada.paciente} • Especialidad: ${visitaSolicitada.especialidad}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Motivo: ${visitaSolicitada.motivo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.10f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "✅ Solicitud registrada. Notificación enviada a cuidadores.ies@gmail.com. El médico confirmará el turno.",
                                fontSize = 11.sp,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Próximas Visitas Médicas",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Sin turnos",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aún no tienes visitas agendadas para los próximos días. Presioná el botón de abajo para solicitar una visita con nuestro equipo médico.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Botón Destacado: Solicitar Visita Médica
            Button(
                onClick = onSolicitarVisitaClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (visitaSolicitada != null) "Solicitar Otra Visita Médica" else "Solicitar Visita Médica (Vía Gmail)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Consultas Disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ActionNavCard(
                titulo = "Agenda de Turnos Confirmados",
                subtitulo = "Ver horarios de médicos y terapeutas agendados",
                icono = Icons.Default.CalendarMonth,
                colorIcono = MaterialTheme.colorScheme.secondary,
                tag = "Confirmados",
                onClick = onNavigateToTurnosCalendar
            )

            ActionNavCard(
                titulo = "Ficha Médica (Modo Lectura)",
                subtitulo = "Ver diagnósticos y medicamentos indicados",
                icono = Icons.Default.Person,
                colorIcono = MaterialTheme.colorScheme.primary,
                tag = "Solo Lectura",
                onClick = onNavigateToPatients
            )

            // Contactos de Emergencia
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Emergencia",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Guardia Clínica Lumina",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Línea directa familiares: (011) 4567-8900\nCorreo: cuidadores.ies@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        // MODO DEMO (familiar@lumina.com - Roberto Gómez)
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Ficha del Paciente Ser Querido
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👴", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Roberto Gómez",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "82 años • Habitación 204 • Estado: Estable",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Último Estado Reportado por el Personal:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Almuerzo: 100% ingerido (Dieta hiposódica)\n• Hidratación: 1.800 ml completados\n• Descanso favorable y sin incidencias clínicas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tarjeta de Próxima Visita Médica Confirmada (1h de atención)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Próxima Visita Médica Confirmada",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Mañana 1h",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mañana • 09:30 - 10:30 hs (Dr. Alejandro Gómez)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Motivo: Evaluación médica geriátrica semestral",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón Destacado: Solicitar Visita Médica
            Button(
                onClick = onSolicitarVisitaClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Solicitar Visita Médica (Vía Gmail)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Consultas Disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ActionNavCard(
                titulo = "Próximas Visitas Confirmadas",
                subtitulo = "Ver horarios de médicos y terapeutas agendados",
                icono = Icons.Default.CalendarMonth,
                colorIcono = MaterialTheme.colorScheme.secondary,
                tag = "Confirmados",
                onClick = onNavigateToTurnosCalendar
            )

            ActionNavCard(
                titulo = "Ficha Médica (Modo Lectura)",
                subtitulo = "Ver diagnósticos y medicamentos indicados",
                icono = Icons.Default.Person,
                colorIcono = MaterialTheme.colorScheme.primary,
                tag = "Solo Lectura",
                onClick = onNavigateToPatients
            )

            // Contactos de Emergencia
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Emergencia",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Guardia Clínica Lumina",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Línea directa familiares: (011) 4567-8900",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// =======================================================================
// COMPONENTES REUTILIZABLES: KPI CARD Y ACTION NAV CARD UNIFORMES
// =======================================================================
@Composable
private fun KpiCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(118.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = titulo,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ActionNavCard(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorIcono.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = colorIcono,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = colorIcono.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorIcono,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
