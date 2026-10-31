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

    if (mostrarDialogoVisita) {
        var especialidadSeleccionada by remember { mutableStateOf("Medicina General") }
        var motivoVisita by remember { mutableStateOf("") }

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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Paciente: Roberto Gómez (Habitación 204)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Se enviará una solicitud al Gmail administrativo (administracion@lumina.salud.org). Una vez confirmada por el equipo médico, recibirás una notificación de confirmación en tu correo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = motivoVisita,
                        onValueChange = { motivoVisita = it },
                        label = { Text("Motivo o consulta especial") },
                        placeholder = { Text("Ej: Control de presión y marcha...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
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
                    text = "Tu solicitud ha sido enviada con ééxito al Gmail de guardia médica. Te notificaremos al correo electrónico tan pronto como el turno sea agendado en el calendario.",
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
                                            text = "¡¡Bienvenido/a!",
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
                            onNavigateToIngesta = onNavigateToIngesta,
                            onNavigateToCrearReporte = { onNavigateToCrearReporte(101L) }
                        )
                    }
                }
                UserRole.USUARIO -> {
                    item {
                        UsuarioSection(
                            onNavigateToTurnosCalendar = onNavigateToTurnosCalendar,
                            onNavigateToPatients = onNavigateToPatients,
                            onSolicitarVisitaClick = { mostrarDialogoVisita = true }
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

// =======================================================================
// SECCIÓN 2: VISTA DE EMPLEADO (TURNOS DESDE HOY EN ADELANTE)
// =======================================================================
@Composable
private fun EmpleadoSection(
    onNavigateToPatients: () -> Unit,
    onNavigateToTurnosCalendar: () -> Unit,
    onNavigateToIngesta: () -> Unit,
    onNavigateToCrearReporte: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Tarjeta de Guardia Asignada
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Turno Activo Hoy",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981)
                    ) {
                        Text(
                            text = "En Servicio",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "08:00 - 16:00 hs • Paciente: Roberto Gómez (Hab. 204)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            subtitulo = "Cronograma filtrado de visitas y guardias futuras",
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

// =======================================================================
// SECCIÓN 3: VISTA DE USUARIO (PORTAL FAMILIAR - ROBERTO GÓMEZ)
// =======================================================================
@Composable
private fun UsuarioSection(
    onNavigateToTurnosCalendar: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onSolicitarVisitaClick: () -> Unit
) {
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
