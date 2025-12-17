package com.milsabores.appkotlin_guia.repository

import com.milsabores.appkotlin_guia.model.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val dao: OrderDao
) {
    /** Guarda una nueva orden en Room y devuelve el id autogenerado. */
    suspend fun save(order: OrderEntity): Long = dao.insert(order)

    /** Observa todas las órdenes, ordenadas por fecha de creación (más recientes primero). */
    fun observeOrders(): Flow<List<OrderEntity>> = dao.getAll()
}
