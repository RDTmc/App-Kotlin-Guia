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

    // Soportamos ambos nombres que puede mandar el backend
    @Json(name = "categoryId") val categoryId: Int? = null,
    @Json(name = "category_id") val categoryIdSnake: Int? = null,

    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Int,

    @Json(name = "imagePath") val imagePath: String? = null,
    @Json(name = "image_path") val imagePathSnake: String? = null,

    @Json(name = "description") val description: String? = null,
    @Json(name = "tags") val tags: List<String>? = null,
    @Json(name = "sizes") val sizes: List<String>? = null
)

/** Mapper: API → Dominio (en español) */
fun ProductDto.toDomain(baseImageUrl: String? = null): Product {
    // 1) Resolver categoría desde camelCase o snake_case
    val categoriaIdResuelta = (categoryId ?: categoryIdSnake)?.toString() ?: "Sin categoría"

    // 2) Resolver ruta de imagen desde camelCase o snake_case
    val rawImagePath = imagePath ?: imagePathSnake

    // 3) Normalizar imagen:
    //    - Si viene URL absoluta (http/https) → se usa tal cual (Supabase)
    //    - Si viene relativa ("img/pg_brownie.png") + baseImageUrl → se concatena
    //    - Si no hay nada → string vacío
    val imagenFinal = when {
        rawImagePath.isNullOrBlank() -> ""
        rawImagePath.startsWith("http", ignoreCase = true) -> rawImagePath
        baseImageUrl.isNullOrBlank() -> rawImagePath
        else -> {
            val base = baseImageUrl.trimEnd('/')
            val path = rawImagePath.trimStart('/')
            "$base/$path"
        }
    }

    return Product(
        id = id,
        categoria = categoriaIdResuelta,
        nombre = name,
        precio = price,
        imagen = imagenFinal,
        descripcion = description.orEmpty(),
        tags = tags ?: emptyList(),
        tamanos = sizes ?: emptyList()
    )
}

/** Saca directamente la lista de dominio desde la página */
fun PagedProductsDto.toDomainList(baseImageUrl: String? = null): List<Product> =
    items.map { it.toDomain(baseImageUrl) }
