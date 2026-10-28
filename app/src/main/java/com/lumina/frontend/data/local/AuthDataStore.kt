package com.lumina.frontend.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthDataStore(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val ROL_KEY = stringPreferencesKey("user_rol")
        val EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    val rolFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ROL_KEY]
    }

    val emailFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }

    fun getAccessToken(): String? = runBlocking {
        tokenFlow.firstOrNull()
    }

    fun getRefreshToken(): String? = runBlocking {
        refreshTokenFlow.firstOrNull()
    }

    suspend fun saveAuthData(token: String, rol: String, email: String, refreshToken: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROL_KEY] = rol
            preferences[EMAIL_KEY] = email
            if (refreshToken != null) {
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
        }
    }

    fun saveTokensSync(token: String, refreshToken: String? = null) = runBlocking {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            if (refreshToken != null) {
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(ROL_KEY)
            preferences.remove(EMAIL_KEY)
        }
    }

    fun clearAuthDataSync() = runBlocking {
        clearAuthData()
    }
}
