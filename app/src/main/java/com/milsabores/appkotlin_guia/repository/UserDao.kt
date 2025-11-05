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
    suspend fun obtenerUsuarioPorId(id: Int): Users?

    @Query("SELECT * FROM users WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): Users?

    @Query("SELECT * FROM users WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun buscarPorCredenciales(correo: String, contrasena: String): Users?

    @Insert
    suspend fun agregarUsuario(user: Users)

    @Update
    suspend fun actualizarUsuario(user: Users)

    @Delete
    suspend fun eliminarUsuario(user: Users)

    @Query("UPDATE users SET contrasena = :nueva WHERE correo = :correo")
    suspend fun actualizarPasswordPorCorreo(correo: String, nueva: String)

}