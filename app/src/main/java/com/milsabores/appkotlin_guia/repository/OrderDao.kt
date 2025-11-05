package com.milsabores.appkotlin_guia.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.milsabores.appkotlin_guia.model.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: OrderEntity): Long

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAll(): Flow<List<OrderEntity>>
}
