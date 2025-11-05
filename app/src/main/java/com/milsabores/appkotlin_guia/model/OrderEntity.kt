package com.milsabores.appkotlin_guia.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val address: String,
    val date: String?,
    val time: String?,
    val payment: String,
    val subtotal: Int,
    val iva: Int,
    val shipping: Int,
    val discount: Int,
    val total: Int
)
