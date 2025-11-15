package com.milsabores.appkotlin_guia.data.remote.dto

import com.milsabores.appkotlin_guia.model.Product
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PagedProductsDto(
    @Json(name = "items") val items: List<ProductDto>,
    @Json(name = "page") val page: Int,
    @Json(name = "size") val size: Int,
    @Json(name = "totalItems") val totalItems: Int,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "hasNext") val hasNext: Boolean
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: String,
    @Json(name = "categoryId") val categoryId: Int?,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Int,
    @Json(name = "imagePath") val imagePath: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "tags") val tags: List<String>?,
    @Json(name = "sizes") val sizes: List<String>?
)

/** Mapper: API → Dominio (en español) */
fun ProductDto.toDomain(baseImageUrl: String? = null): Product {
    // Si quisieras prefijar URL absolutas para imágenes relativas (img/...):
    // val img = when {
    //     imagePath.isNullOrBlank() -> ""
    //     imagePath.startsWith("http", ignoreCase = true) -> imagePath
    //     else -> "${baseImageUrl.orEmpty()}${if (baseImageUrl?.endsWith('/') == true) "" else "/"}$imagePath"
    // }
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

/** Saca directamente la lista de dominio desde la página */
fun PagedProductsDto.toDomainList(baseImageUrl: String? = null): List<Product> =
    items.map { it.toDomain(baseImageUrl) }
