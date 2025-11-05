package com.milsabores.appkotlin_guia.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Carrito local por usuario invitado/logueado.
 * Para MVP usamos un solo carrito (id = 1).
 */
@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val name: String,
    val image: String? = null,
    val size: String? = null,
    val flavor: String? = null,
    val quantity: Int,
    val unitPrice: Int
)
