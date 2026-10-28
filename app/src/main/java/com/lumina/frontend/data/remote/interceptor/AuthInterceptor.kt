package com.lumina.frontend.data.remote.interceptor

import com.lumina.frontend.data.local.AuthDataStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authDataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si la petición ya tiene el header Authorization o es para autenticación pública, continuar sin modificar
        if (originalRequest.header("Authorization") != null ||
            originalRequest.url.encodedPath.contains("/api/auth/login") ||
            originalRequest.url.encodedPath.contains("/api/auth/refresh")
        ) {
            return chain.proceed(originalRequest)
        }

        val token = authDataStore.getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
