package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.ViewModel
import com.milsabores.appkotlin_guia.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ProductDetailUiState(
    val product: Product? = null,
    val tamanos: List<String> = emptyList(),
    val sabores: List<String> = listOf("Vainilla", "Chocolate", "Frutos rojos"),
    val selectedTamano: String? = null,
    val selectedSabor: String? = null,
    val mensaje: String = "",
    val mensajeCount: Int = 0,
    val mensajeError: String? = null,
    val qty: Int = 1,
    val showShine: Boolean = false,   // micro-animación
    val showZoom: Boolean = false     // modal zoom
)

class ProductDetailViewModel : ViewModel() {

    private val _ui = MutableStateFlow(ProductDetailUiState())
    val ui: StateFlow<ProductDetailUiState> = _ui

    // guardamos el VM fuente para "similares"
    private var sourceCatalogVm: CatalogViewModel? = null

    /** Cargar datos al entrar a la pantalla */
    fun load(productId: String, catalogVm: CatalogViewModel) {
        sourceCatalogVm = catalogVm
        val p = catalogVm.getProduct(productId)
        val sizes = (p?.tamanos?.takeIf { it.isNotEmpty() } ?: listOf("Chico", "Mediano", "Grande"))
        _ui.update {
            it.copy(
                product = p,
                tamanos = sizes,
                selectedTamano = sizes.firstOrNull()
            )
        }
    }

    fun setTamano(t: String) = _ui.update { it.copy(selectedTamano = t) }
    fun setSabor(s: String) = _ui.update { it.copy(selectedSabor = s) }

    fun setMensaje(text: String) {
        val trimmed = text.take(30)
        val error = if (text.length > 30) "Máximo 30 caracteres" else null
        _ui.update { it.copy(mensaje = trimmed, mensajeCount = trimmed.length, mensajeError = error) }
    }

    fun incQty(stockMax: Int = 10) = _ui.update {
        val next = (it.qty + 1).coerceAtMost(stockMax)
        it.copy(qty = next, showShine = next != it.qty)
    }

    fun decQty() = _ui.update { it.copy(qty = (it.qty - 1).coerceAtLeast(1)) }

    fun consumeShine() = _ui.update { it.copy(showShine = false) }

    fun setZoom(show: Boolean) = _ui.update { it.copy(showZoom = show) }

    fun similar(): List<Product> {
        val p = ui.value.product ?: return emptyList()
        val cat = sourceCatalogVm ?: return emptyList()
        return cat.getSimilar(p)
    }
}
