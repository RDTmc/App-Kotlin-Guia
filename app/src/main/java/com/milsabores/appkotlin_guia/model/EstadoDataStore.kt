package com.milsabores.appkotlin_guia.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// nombre para el DataStore
private const val PREFERENCIAS_USUARIO = "preferencias_usuario"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCIAS_USUARIO)

class EstadoDataStore(private val context: Context) {

    // claves
    private val ESTADO_BOTON        = booleanPreferencesKey("estado_boton")
    private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    private val KEY_GUEST_MODE      = booleanPreferencesKey("guest_mode")
    private val KEY_IS_LOGGED_IN    = booleanPreferencesKey("is_logged_in")
    private val KEY_USER_EMAIL      = stringPreferencesKey("user_email")

    // Lecturas
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDING_DONE] ?: false }
    val guestMode: Flow<Boolean>      = context.dataStore.data.map { it[KEY_GUEST_MODE] ?: false }
    val isLoggedIn: Flow<Boolean>     = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }
    val userEmail: Flow<String?>      = context.dataStore.data.map { it[KEY_USER_EMAIL] }

    // Escrituras
    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = done }
    }

    suspend fun resetOnboarding() {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = false }
    }

    suspend fun setGuestMode(on: Boolean) {
        context.dataStore.edit { it[KEY_GUEST_MODE] = on }
    }

    suspend fun setLoggedIn(on: Boolean) {
        context.dataStore.edit { it[KEY_IS_LOGGED_IN] = on }
    }

    suspend fun setUserEmail(email: String?) {
        context.dataStore.edit { prefs ->
            if (email.isNullOrBlank()) prefs.remove(KEY_USER_EMAIL)
            else prefs[KEY_USER_EMAIL] = email
        }
    }

    /** Cerrar sesión real (borra login y email; opcionalmente quita modo invitado). */
    suspend fun clearSession() {
        context.dataStore.edit {
            it[KEY_IS_LOGGED_IN] = false
            it.remove(KEY_USER_EMAIL)
            it[KEY_GUEST_MODE] = false
        }
    }

    // modo especial existente
    suspend fun guardarEstado(nuevoEstado: Boolean) {
        context.dataStore.edit { pref -> pref[ESTADO_BOTON] = nuevoEstado }
    }

    fun obtenerEstado(): Flow<Boolean?> = context.dataStore.data.map { it[ESTADO_BOTON] }
}
