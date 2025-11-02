package com.milsabores.appkotlin_guia.model


data class Product(
    val id: String,
    val categoria: String,
    val nombre: String,
    val precio: Int,
    val imagen: String,      // ej: "img/tt_vainilla.png" → drawable "tt_vainilla"
    val descripcion: String,
    val tags: List<String> = emptyList(),
    val rating: Double = 4.8,
    val tamanos: List<String> = emptyList()
)

enum class HomeFilter {
    CUMPLEANOS, BODAS, SIN_AZUCAR, VEGANO, TODOS
}