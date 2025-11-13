package com.milsabores.appkotlin_guia.data.remote.dto

import com.milsabores.appkotlin_guia.model.Product

data class PagedProductsDto(
    val items: List<ProductDto>,
    val page: Int,
    val size: Int,
    val totalItems: Int,
    val totalPages: Int,
    val hasNext: Boolean
)

data class ProductDto(
    val id: String,
    val categoryId: Int?,
    val name: String,
    val price: Int,
    val imagePath: String?,
    val description: String?,
    val tags: List<String>?,
    val sizes: List<String>?
)

/** Mapper: API → Dominio (en español) */
fun ProductDto.toDomain(baseImageUrl: String? = null): Product {
    // Si tu backend sirve imágenes relativas (img/...), puedes prefijar una base:
    // val img = if (!imagePath.isNullOrBlank() && !imagePath.startsWith("http"))
    //              "${baseImageUrl ?: ""}${imagePath}"
    //           else imagePath.orEmpty()
    val img = imagePath.orEmpty()

    return Product(
        id = id,
        categoria = categoryId?.toString() ?: "Sin categoría",
        nombre = name,
        precio = price,
        imagen = img,
        descripcion = description.orEmpty(),
        tags = tags ?: emptyList(),
        tamanos = sizes ?: emptyList()
    )
}

/** Saca directamente la lista de dominio desde la página  */
fun PagedProductsDto.toDomainList(baseImageUrl: String? = null): List<Product> =
    items.map { it.toDomain(baseImageUrl) }
