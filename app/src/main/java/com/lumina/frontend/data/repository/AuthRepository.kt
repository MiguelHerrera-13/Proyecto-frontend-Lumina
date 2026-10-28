package com.lumina.frontend.data.repository

import com.lumina.frontend.data.local.AuthDataStore
import com.lumina.frontend.data.remote.AuthApiService
import com.lumina.frontend.data.remote.RetrofitClient
import com.lumina.frontend.data.remote.dto.AuthResponseDto
import com.lumina.frontend.data.remote.dto.LoginRequestDto
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val authDataStore: AuthDataStore,
    private val authApiService: AuthApiService = RetrofitClient.authApiService
) {

    val token: Flow<String?> = authDataStore.tokenFlow
    val rol: Flow<String?> = authDataStore.rolFlow
    val email: Flow<String?> = authDataStore.emailFlow

    suspend fun login(email: String, password: String): Result<AuthResponseDto> {
        return try {
            val response = authApiService.login(LoginRequestDto(email = email.trim(), password = password))
            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!
                authDataStore.saveAuthData(
                    token = authData.token,
                    rol = authData.rol,
                    email = authData.email,
                    refreshToken = authData.refreshToken
                )
                Result.success(authData)
            } else if (response.code() == 401) {
                Result.failure(Exception("Credenciales inválidas: correo o contraseña incorrectos"))
            } else {
                Result.failure(Exception("Error al iniciar sesión: código ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: no se pudo comunicar con el servidor"))
        }
    }

    suspend fun logout() {
        authDataStore.clearAuthData()
    }
}
