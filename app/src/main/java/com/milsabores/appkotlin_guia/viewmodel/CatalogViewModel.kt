package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milsabores.appkotlin_guia.data.remote.CatalogRemoteRepository
import com.milsabores.appkotlin_guia.model.Product
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val featured: List<Product> = emptyList(),
    val filter: String? = null,            // "Todos", "Cumpleaños", etc. (opcional)
    val isLoading: Boolean = false,
    val error: String? = null
)

class CatalogViewModel(
    private val remote: CatalogRemoteRepository = CatalogRemoteRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(CatalogUiState())
    val ui: StateFlow<CatalogUiState> = _ui

    private var loadJob: Job? = null
    private var fullCatalogCache: List<Product> = emptyList() // fuente para filtros locales

    init {
        // Carga inicial
        loadFromApi()
    }

    /** Carga desde API real y actualiza el estado. */
    fun loadFromApi(page: Int = 0, size: Int = 12, q: String? = null, categoryId: Int? = null, sort: String? = null) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)

            try {
                val list = remote.fetchProducts(page = page, size = size, q = q, categoryId = categoryId, sort = sort)
                fullCatalogCache = list

                // Puedes elegir 3 destacados simples (por precio, por orden, etc.)
                val destacados = list.take(3)

                _ui.value = _ui.value.copy(
                    products = list,
                    featured = destacados,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    products = emptyList(),
                    featured = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Error al cargar catálogo"
                )
            }
        }
    }

    /** Filtro local en memoria (no golpea la API). */
    fun setFilter(filter: String?) {
        _ui.value = _ui.value.copy(filter = filter)

        val base = fullCatalogCache
        val filtered = when (filter) {
            null, "Todos" -> base
            "Cumpleaños" -> base.filter { it.nombre.contains("cumple", true) || it.tags.any { t -> t.equals("cumpleaños", true) } }
            "Bodas" -> base.filter { it.nombre.contains("boda", true) || it.tags.any { t -> t.equals("boda", true) } }
            "Sin azúcar" -> base.filter {
                it.categoria.equals("Sin Azúcar", true) ||
                        it.tags.any { t -> t.equals("sin azúcar", true) || t.equals("sinazucar", true) }
            }
            "Vegano" -> base.filter {
                it.categoria.equals("Vegano", true) || it.tags.any { t -> t.equals("vegano", true) }
            }
            else -> base
        }

        // Mantén featured simple (primeros 3 del resultado filtrado)
        _ui.value = _ui.value.copy(
            products = filtered,
            featured = filtered.take(3)
        )
    }

    /** Utilidad para obtener un producto por id (desde cache local). */
    fun getProduct(id: String): Product? =
        (fullCatalogCache).firstOrNull { it.id == id }

    /** Similares por categoría o tags, desde la cache. */
    fun getSimilar(to: Product, limit: Int = 10): List<Product> =
        fullCatalogCache
            .filter { it.id != to.id && (it.categoria == to.categoria || it.tags.any { t -> to.tags.contains(t) }) }
            .take(limit)
}
