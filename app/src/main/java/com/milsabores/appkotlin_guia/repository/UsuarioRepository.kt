package com.milsabores.appkotlin_guia.repository

import android.content.Context
import com.milsabores.appkotlin_guia.model.Users
import kotlinx.coroutines.flow.Flow
class UsuarioRepository private constructor(context: Context) {
    private val dao = AppDataBase.getInstance(context).userDao()

    // Listados reactivas (Flow)
    fun listar(): Flow<List<Users>> = dao.obtenerUsuarios()

    // Lecturas puntuales (suspend)
    suspend fun porId(id: Int): Users? = dao.obtenerUsuarioPorId(id)
    suspend fun porCorreo(correo: String): Users? = dao.obtenerUsuarioPorCorreo(correo)
    suspend fun porCredenciales(correo: String, contrasena: String): Users? =
        dao.buscarPorCredenciales(correo, contrasena)

    // CRUD
    suspend fun crear(user: Users) = dao.agregarUsuario(user)
    suspend fun actualizar(user: Users) = dao.actualizarUsuario(user)
    suspend fun eliminar(user: Users) = dao.eliminarUsuario(user)

    companion object {
        @Volatile private var INSTANCE: UsuarioRepository? = null
        fun get(context: Context): UsuarioRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UsuarioRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}