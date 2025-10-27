package com.milsabores.appkotlin_guia.repository

import android.content.Context
import com.milsabores.appkotlin_guia.model.Users
import kotlinx.coroutines.flow.Flow

class UsuarioRepository private constructor(context: Context) {
    private val dao = AppDataBase.getInstance(context).userDao()

    fun listar(): Flow<List<Users>> = dao.obtenerUsuarios()
    fun porId(id: Int): Flow<Users?> = dao.obtenerUsuarioPorId(id)
    fun porCorreo(correo: String): Flow<Users> = dao.obtenerUsuarioPorCorreo(correo)
    fun porCredenciales(correo: String, contrasena: String): Flow<Users?> =
        dao.buscarPorCredenciales(correo, contrasena)

    suspend fun crear(user: Users) = dao.agregarUsuario(user)
    suspend fun actualizar(user: Users) = dao.actualizarUsuario(user)
    suspend fun eliminar(user: Users) = dao.eliminarUsuario(user)

    companion object {
        @Volatile
        private var INSTANCE: UsuarioRepository? = null
        fun get(context: Context): UsuarioRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UsuarioRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}