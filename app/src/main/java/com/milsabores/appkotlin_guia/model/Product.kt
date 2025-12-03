package com.milsabores.appkotlin_guia.model

data class Product(
    val id: String,
    val categoria: String,
    val nombre: String,
    val precio: Int,

    // URL de la imagen (normalmente absoluta desde Supabase),
    // pero también puede ser una ruta relativa si en algún momento la usas así.
    val imagen: String,

    val descripcion: String,
    val tags: List<String> = emptyList(),
    val rating: Double = 4.8,
    val tamanos: List<String> = emptyList()
)

enum class HomeFilter {
    CUMPLEANOS, BODAS, SIN_AZUCAR, VEGANO, TODOS
}
