package com.lumina.frontend.data.remote.interceptor

import com.lumina.frontend.data.local.AuthDataStore
import com.lumina.frontend.data.local.SessionManager
import com.lumina.frontend.data.remote.AuthApiService
import com.lumina.frontend.data.remote.dto.RefreshTokenRequestDto
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val authDataStore: AuthDataStore,
    private val authApiServiceProvider: () -> AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar loops infinitos de reintentos
        if (responseCount(response) >= 3) {
            return null
        }

        // Si la petición que falló fue de login o refresh, no intentar renovar
        val path = response.request.url.encodedPath
        if (path.contains("/api/auth/login") || path.contains("/api/auth/refresh")) {
            return null
        }

        synchronized(this) {
            val currentToken = authDataStore.getAccessToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // Si otro hilo ya renovó el token, reintentar con el nuevo token sin llamar a refresh nuevamente
            if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = authDataStore.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                // No hay refresh token disponible: limpiar sesión y notificar
                authDataStore.clearAuthDataSync()
                SessionManager.notifySessionExpired()
                return null
            }

            return try {
                val refreshCall = authApiServiceProvider().refreshTokenSync(RefreshTokenRequestDto(refreshToken))
                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val authBody = refreshResponse.body()!!
                    val newAccessToken = authBody.token
                    val newRefreshToken = authBody.refreshToken ?: refreshToken

                    // Guardar los nuevos tokens en DataStore
                    authDataStore.saveTokensSync(newAccessToken, newRefreshToken)

                    // Reintentar la llamada original con el nuevo token
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    // El refresh token también expiró o fue revocado: limpiar sesión y navegar a Login
                    authDataStore.clearAuthDataSync()
                    SessionManager.notifySessionExpired()
                    null
                }
            } catch (e: Exception) {
                authDataStore.clearAuthDataSync()
                SessionManager.notifySessionExpired()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
