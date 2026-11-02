package com.lumina.frontend.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    val BASE_URL: String = if (
        android.os.Build.FINGERPRINT.startsWith("generic") ||
        android.os.Build.MODEL.contains("google_sdk") ||
        android.os.Build.MODEL.contains("Emulator") ||
        android.os.Build.HARDWARE.contains("goldfish") ||
        android.os.Build.HARDWARE.contains("ranchu")
    ) {
        "http://10.0.2.2:8080/"
    } else {
        "http://127.0.0.1:8080/"
    }

    // Bandera reactiva de disponibilidad del backend local
    @Volatile
    var isBackendAvailable: Boolean = true
        private set

    fun retryBackendConnection() {
        isBackendAvailable = true
    }

    // Interceptor para inyectar token JWT automáticamente si el usuario inició sesión
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = com.lumina.frontend.data.local.SessionManager.currentSession.value?.token
        val req = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        chain.proceed(req)
    }

    // Cliente OkHttp con timeout estándar para respuesta en desarrollo físico y backend local
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val pacienteApiService: PacienteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PacienteApiService::class.java)
    }

    val usuarioApiService: UsuarioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioApiService::class.java)
    }

    val authApiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
    
    val turnoApiService: TurnoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TurnoApiService::class.java)
    }

    val reporteDiarioApiService: ReporteDiarioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReporteDiarioApiService::class.java)
    }
}
