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

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Bandera reactiva de disponibilidad del backend local
    @Volatile
    var isBackendAvailable: Boolean = true
        private set

    fun retryBackendConnection() {
        isBackendAvailable = true
    }

    // Interceptor inteligente: Si el backend ya falló, no espera 2s por cada llamada
    private val fastFailInterceptor = Interceptor { chain ->
        if (!isBackendAvailable) {
            throw IOException("Modo fuera de línea activo: backend no disponible")
        }
        try {
            val response = chain.proceed(chain.request())
            isBackendAvailable = true
            response
        } catch (e: Exception) {
            if (e is SocketTimeoutException || e is ConnectException || e is IOException) {
                isBackendAvailable = false
            }
            throw e
        }
    }

    // Cliente OkHttp con timeout optimizado (2 segundos) para respuesta inmediata en desarrollo físico y fallback
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(fastFailInterceptor)
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
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
