package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.navigation.AppRoute
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
    val showShine: Boolean = false,      // micro-animación
    val showZoom: Boolean = false        // modal zoom
)

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val catalogVm: CatalogViewModel = CatalogViewModel() // para MVP; ideal inyectar
) : ViewModel() {

    private val _ui = MutableStateFlow(ProductDetailUiState())
    val ui: StateFlow<ProductDetailUiState> = _ui

    init {
        val id = savedStateHandle.get<String>(AppRoute.Product.ARG_ID)
        val p = id?.let { catalogVm.getProduct(it) }
        _ui.update {
            val sizes = (p?.tamanos?.takeIf { it.isNotEmpty() } ?: listOf("Chico", "Mediano", "Grande"))
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
        val shine = next != it.qty // activar brillo si subió
        it.copy(qty = next, showShine = shine)
    }

    fun decQty() = _ui.update { it.copy(qty = (it.qty - 1).coerceAtLeast(1)) }

    fun consumeShine() = _ui.update { it.copy(showShine = false) }

    fun setZoom(show: Boolean) = _ui.update { it.copy(showZoom = show) }

    fun similar(): List<Product> = ui.value.product?.let { catalogVm.getSimilar(it) } ?: emptyList()
}