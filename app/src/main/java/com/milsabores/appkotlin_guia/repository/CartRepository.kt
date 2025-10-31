package com.milsabores.appkotlin_guia.repository

import com.milsabores.appkotlin_guia.model.CartEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val dao: CartDao
) {
    fun observeCart(): Flow<List<CartEntity>> = dao.getAll()

    suspend fun getCartOnce(): List<CartEntity> = dao.getAllOnce()

    suspend fun add(item: CartEntity) {
        dao.insert(item)
    }

    suspend fun update(item: CartEntity) {
        dao.update(item)
    }

    suspend fun delete(id: Long) {
        dao.delete(id)
    }

    suspend fun clear() {
        dao.clear()
    }
}