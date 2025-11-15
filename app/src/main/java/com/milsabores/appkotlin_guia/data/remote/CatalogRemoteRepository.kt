package com.milsabores.appkotlin_guia.data.remote

import com.milsabores.appkotlin_guia.data.remote.dto.toDomainList
import com.milsabores.appkotlin_guia.model.Product

/**
 * Fuente remota del catálogo (Retrofit).
 * Mantiene la mínima superficie: lista y detalle (si lo necesitas luego).
 */
class CatalogRemoteRepository(
    private val api: ApiService = ApiClient.service
) {

    /**
     * Trae una página de productos y la mapea al dominio.
     * @param baseImageUrl opcional para prefijar rutas relativas ("img/..."), si te interesa.
     */
    suspend fun fetchProducts(
        page: Int = 0,
        size: Int = 12,
        q: String? = null,
        categoryId: Int? = null,
        sort: String? = null,
        baseImageUrl: String? = null
    ): List<Product> {
        val paged = api.getProducts(page = page, size = size, q = q, categoryId = categoryId, sort = sort)
        return paged.toDomainList(baseImageUrl)
    }

    // (Opcional para próximas iteraciones)
    // suspend fun fetchProductById(id: String, baseImageUrl: String? = null): Product {
    //     val dto = api.getProductById(id)
    //     return dto.toDomain(baseImageUrl)
    // }
}
