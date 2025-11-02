package com.milsabores.appkotlin_guia.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    // 🔹 Flujos de lectura
    val onboardingDone: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_ONBOARDING_DONE] ?: false }

    val guestMode: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_GUEST_MODE] ?: false }

    // sesión real (login)
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_IS_LOGGED_IN] ?: false }

    // 🔹 Escrituras
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

    // modo especial que ya tenías
    suspend fun guardarEstado(nuevoEstado: Boolean) {
        context.dataStore.edit { pref ->
            pref[ESTADO_BOTON] = nuevoEstado
        }
    }

    fun obtenerEstado(): Flow<Boolean?> {
        return context.dataStore.data.map { pref ->
            pref[ESTADO_BOTON]
        }
    }
}
