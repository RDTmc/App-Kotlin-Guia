package com.milsabores.appkotlin_guia.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.milsabores.appkotlin_guia.model.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun obtenerUsuarios(): Flow<List<Users>>
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun obtenerUsuarioPorId(id: Int): Flow<Users>
    @Query("SELECT * FROM users WHERE correo = :correo LIMIT 1")
    fun obtenerUsuarioPorCorreo(correo: String): Flow<Users>
    @Query("SELECT * FROM users WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    fun buscarPorCredenciales(correo: String, contrasena: String): Flow<Users?>
    @Insert
    suspend fun agregarUsuario(user: Users)
    @Update
    suspend fun actualizarUsuario(user: Users)
    @Delete
    suspend fun eliminarUsuario(user: Users)

}