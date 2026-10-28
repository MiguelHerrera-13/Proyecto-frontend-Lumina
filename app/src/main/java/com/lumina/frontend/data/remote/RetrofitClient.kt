package com.lumina.frontend.data.remote

import android.content.Context
import com.lumina.frontend.data.local.AuthDataStore
import com.lumina.frontend.data.remote.interceptor.AuthInterceptor
import com.lumina.frontend.data.remote.interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Volatile
    private var authDataStore: AuthDataStore? = null

    fun initialize(context: Context) {
        if (authDataStore == null) {
            synchronized(this) {
                if (authDataStore == null) {
                    authDataStore = AuthDataStore(context.applicationContext)
                }
            }
        }
    }

    // Cliente base sin autenticación para llamadas públicas (login y refresh de token)
    private val rawOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val rawRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(rawOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        rawRetrofit.create(AuthApiService::class.java)
    }

    // Cliente autenticado con AuthInterceptor y TokenAuthenticator
    private val authenticatedOkHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        authDataStore?.let { dataStore ->
            builder.addInterceptor(AuthInterceptor(dataStore))
            builder.authenticator(TokenAuthenticator(dataStore) { authApiService })
        }
        builder.build()
    }

    private val authenticatedRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val pacienteApiService: PacienteApiService by lazy {
        authenticatedRetrofit.create(PacienteApiService::class.java)
    }

    val usuarioApiService: UsuarioApiService by lazy {
        authenticatedRetrofit.create(UsuarioApiService::class.java)
    }

    val turnoApiService: TurnoApiService by lazy {
        authenticatedRetrofit.create(TurnoApiService::class.java)
    }
}
