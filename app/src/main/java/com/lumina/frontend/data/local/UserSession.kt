package com.lumina.frontend.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Roles del sistema clínico Lúmina.
 */
enum class UserRole(
    val code: String,
    val titulo: String,
    val subtitulo: String,
    val icono: String,
    val colorHex: Long
) {
    SUPER_ADMIN(
        code = "SUPER_ADMIN",
        titulo = "Super Admin",
        subtitulo = "Acceso global: Pacientes, Turnos, Métricas y Gestión total",
        icono = "👑",
        colorHex = 0xFF7C3AED // Violeta elegante
    ),
    EMPLEADO(
        code = "EMPLEADO",
        titulo = "Empleado",
        subtitulo = "Operativa clínica: Pacientes asignados, Turnos del día, Reporte Stepper e Ingesta",
        icono = "🩺",
        colorHex = 0xFF0284C7 // Cian médico
    ),
    USUARIO(
        code = "USUARIO",
        titulo = "Familiar",
        subtitulo = "Portal familiar: Próximos turnos, Ficha médica e Historial de cuidados",
        icono = "👤",
        colorHex = 0xFF0D9488 // Verde azulado bienestar
    )
}

/**
 * Modelo de datos de la sesión activa del usuario.
 */
data class UserSessionData(
    val email: String,
    val nombre: String,
    val rol: UserRole,
    val token: String? = null
)

/**
 * Gestor reactivo de sesión en memoria para Lúmina.
 * Diseñado de forma no invasiva para no interferir con las issues de red o Room de otros integrantes.
 */
object SessionManager {

    private val _currentSession = MutableStateFlow<UserSessionData?>(null)
    val currentSession: StateFlow<UserSessionData?> = _currentSession.asStateFlow()

    /**
     * Inicia sesión con el rol y correo provisto.
     */
    fun login(email: String, rol: UserRole, nombrePersonalizado: String? = null, token: String? = null) {
        val nombre = nombrePersonalizado ?: when (rol) {
            UserRole.SUPER_ADMIN -> "Dr. Miguel Herrera"
            UserRole.EMPLEADO -> "Lic. Camila Salomón"
            UserRole.USUARIO -> "Carlos Gómez"
        }
        _currentSession.value = UserSessionData(
            email = email.trim(),
            nombre = nombre,
            rol = rol,
            token = token
        )
    }

    /**
     * Cierra la sesión activa de forma segura.
     */
    fun logout() {
        _currentSession.value = null
    }

    /**
     * Determina si hay una sesión activa.
     */
    fun isLogged(): Boolean = _currentSession.value != null

    /**
     * Obtiene el rol actual o retorna null si no ha iniciado sesión.
     */
    fun getCurrentRole(): UserRole? = _currentSession.value?.rol
}

