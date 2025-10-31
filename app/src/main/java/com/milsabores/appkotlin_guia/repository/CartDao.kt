package com.milsabores.appkotlin_guia.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.milsabores.appkotlin_guia.model.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartEntity): Long

    @Update
    suspend fun update(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    @Query("SELECT * FROM cart_items")
    suspend fun getAllOnce(): List<CartEntity>
}