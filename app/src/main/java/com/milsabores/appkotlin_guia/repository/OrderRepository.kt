package com.milsabores.appkotlin_guia.repository

import com.milsabores.appkotlin_guia.model.OrderEntity

class OrderRepository(
    private val dao: OrderDao
) {
    suspend fun save(order: OrderEntity): Long = dao.insert(order)
}
