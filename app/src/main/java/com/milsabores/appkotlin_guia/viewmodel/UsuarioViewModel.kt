package com.milsabores.appkotlin_guia.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.milsabores.appkotlin_guia.model.Users
import com.milsabores.appkotlin_guia.model.UsuarioErrores
import com.milsabores.appkotlin_guia.model.UsuarioUiState
import com.milsabores.appkotlin_guia.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    // Repo con contexto de aplicación
    private val repo: UsuarioRepository = UsuarioRepository.get(application)

    // Declaramos el estado interno mutable
    private val _estado = MutableStateFlow(UsuarioUiState())

    // Expone el estado de manera publica y es de solo lectura
    val estado: StateFlow<UsuarioUiState> = _estado
    val usuariosFlow = repo.listar() // Flow<List<Users>>


    /* ---------- Setters de UI state ---------- */

    // Actualiza el campo nombre
    fun onNombreChange(nuevoNombre: String) {
        _estado.update { it.copy(nombre = nuevoNombre, errores = it.errores.copy(nombre = null)) }
    }

    // Actualiza el campo correo
    fun onCorreoChange(nuevoCorreo: String) {
        _estado.update { it.copy(correo = nuevoCorreo, errores = it.errores.copy(correo = null)) }
    }

    fun onContrasenaChange(nuevaContrasena: String) {
        _estado.update {
            it.copy(
                contrasena = nuevaContrasena,
                errores = it.errores.copy(contrasena = null)
            )
        }
    }

    fun onDireccionChange(nuevaDireccion: String) {
        _estado.update {
            it.copy(
                direccion = nuevaDireccion,
                errores = it.errores.copy(direccion = null)
            )
        }
    }

    fun onAceptarTerminosChange(ok: Boolean) {
        _estado.update { it.copy(aceptaTerminos = ok) }
    }

    /* ---------- Validación Formulario ---------- */

    fun estaValidadoElFormulario(): Boolean {
        //el estado actual del formulario
        val formularioActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (formularioActual.nombre.isBlank()) "El campo es obligatorio" else null,
            correo = if (!Patterns.EMAIL_ADDRESS.matcher(formularioActual.correo)
                    .matches()
            ) "El correo debe ser valido" else null,
            contrasena = if (formularioActual.contrasena.length < 6) "La contraseña debe tener al menos 6 caracteres" else null,
            direccion = if (formularioActual.direccion.isBlank()) "El campo es obligatorio" else null,
        )

        // listOfNotNull retorna una lista de los elementos que "no sean nulos"
        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.contrasena,
            errores.direccion
        ).isNotEmpty() // Retorna true si la coleccion no esta vacia
        _estado.update { it.copy(errores = errores) }
        return if (hayErrores) false
        else true
    }

    /* ---------- Operaciones Room (CRUD) ---------- */

    /** Crear usuario con los datos del estado actual (tras validar y aceptar términos). */

    fun registrarEnDB(onSuccess: (() -> Unit)? = null) {
        val estadoActual = _estado.value
        if (!estaValidadoElFormulario() || !estadoActual.aceptaTerminos) return
        viewModelScope.launch {
            repo.crear(
                Users(
                    nombre = estadoActual.nombre,
                    correo = estadoActual.correo,
                    contrasena = estadoActual.contrasena,
                    direccion = estadoActual.direccion
                )
            )
            onSuccess?.invoke()

        }
    }

    /** Actualizar un usuario existente por id usando los datos del estado. */
    fun actualizarUsuarioExistente(id: Int) {
        val estadoActual = _estado.value
        viewModelScope.launch {
            repo.actualizar(
                Users(
                    id,
                    estadoActual.nombre,
                    estadoActual.correo,
                    estadoActual.contrasena,
                    estadoActual.direccion
                )
            )
        }
    }

    /** Eliminar un usuario por id usando los datos del estado actual. */
    fun eliminarUsuario(id: Int) {
        val estadoActual = _estado.value
        viewModelScope.launch {
            repo.eliminar(
                Users(
                    id,
                    estadoActual.nombre,
                    estadoActual.correo,
                    estadoActual.contrasena,
                    estadoActual.direccion
                )
            )
        }
    }

    /** Cargar datos al estado a partir de un correo. */
    fun cargarUsuarioPorCorreo(correo: String) {
        viewModelScope.launch {
            val user = repo.porCorreo(correo)
            user?.let {
                _estado.update { st ->
                    st.copy(
                        nombre = it.nombre,
                        correo = it.correo,
                        contrasena = it.contrasena,
                        direccion = it.direccion,
                        errores = UsuarioErrores()
                    )
                }
            }
        }
    }

    /** Actualiza nombre/dirección del usuario identificado por su correo en estado.
     *  Devuelve true si se pudo actualizar, false si no existe.
     */
    suspend fun guardarPerfilPorCorreo(): Boolean {
        val s = estado.value
        if (s.correo.isBlank()) return false
        val existente = repo.porCorreo(s.correo) ?: return false
        repo.actualizar(
            existente.copy(
                nombre = s.nombre,
                direccion = s.direccion,
                contrasena = s.contrasena
                // Si quisieras permitir cambiar contraseña aquí, añade: contrasena = s.contrasena
            )
        )
        return true
    }


    /** Login simple: consulta por credenciales y entrega true/false. */
    fun login(correo: String, contrasena: String, onResult: (Boolean, Users?) -> Unit) {
        viewModelScope.launch {
            val user = repo.porCredenciales(correo, contrasena)
            onResult(user != null, user)

        }
    }

    suspend fun cambiarPassword(actual: String, nueva: String, confirmar: String): Boolean {
        val hasUpper = nueva.any { it.isUpperCase() }
        val hasNum   = nueva.any { it.isDigit() }
        if (nueva.length < 8 || !hasUpper || !hasNum || nueva != confirmar) return false

        val correo = estado.value.correo
        val user   = repo.porCredenciales(correo, actual) ?: return false  //

        repo.actualizarPasswordPorCorreo(correo, nueva)
        return true
    }


}
