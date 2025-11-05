package com.milsabores.appkotlin_guia.model

data class CartItem(
    val productId: String,
    val name: String,
    val image: String?,
    val size: String?,
    val flavor: String?,
    val quantity: Int,
    val unitPrice: Int
) {
    val lineTotal: Int get() = quantity * unitPrice
}