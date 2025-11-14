package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milsabores.appkotlin_guia.data.remote.ApiClient
import com.milsabores.appkotlin_guia.data.remote.dto.toDomain
import com.milsabores.appkotlin_guia.model.CartItem
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.ui.util.sizesFor
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val showShine: Boolean = false,
    val showZoom: Boolean = false
)

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle? = null,
    private val catalogVm: CatalogViewModel = CatalogViewModel()   // MVP, ideal inyectar
) : ViewModel() {

    private val _ui = kotlinx.coroutines.flow.MutableStateFlow(ProductDetailUiState())
    val ui: kotlinx.coroutines.flow.StateFlow<ProductDetailUiState> = _ui

    private val api = ApiClient.service
    // private val BASE_IMAGE_URL = "http://10.199.140.94:9090/" // si quieres resolver imágenes

    /**
     * Cargar datos desde catálogo; si no está, fallback remoto /products/{id}.
     */
    fun load(productId: String, catalogVm: CatalogViewModel) {
        // 1) Intentamos resolver desde el catálogo actual (memoria)
        val local = catalogVm.getProduct(productId)
        if (local != null) {
            val sizes = sizesFor(local)
            _ui.update {
                it.copy(
                    product = local,
                    tamanos = sizes,
                    selectedTamano = sizes.firstOrNull()
                )
            }
            return
        }

        // 2) Fallback remoto
        viewModelScope.launch {
            try {
                val dto = api.getProductById(productId)
                // val p = dto.toDomain(BASE_IMAGE_URL)
                val p = dto.toDomain()
                val sizes = sizesFor(p)
                _ui.update {
                    it.copy(
                        product = p,
                        tamanos = sizes,
                        selectedTamano = sizes.firstOrNull()
                    )
                }
            } catch (e: Exception) {
                // puedes exponer un error si lo deseas
                _ui.update { it.copy(mensajeError = "No se pudo cargar el producto") }
            }
        }
    }

    fun setTamano(t: String) {
        _ui.value = _ui.value.copy(selectedTamano = t)
    }

    fun setSabor(s: String) {
        _ui.value = _ui.value.copy(selectedSabor = s)
    }

    fun setMensaje(text: String) {
        val trimmed = text.take(30)
        val error = if (text.length > 30) "Máximo 30 caracteres" else null
        _ui.value = _ui.value.copy(
            mensaje = trimmed,
            mensajeCount = trimmed.length,
            mensajeError = error
        )
    }

    fun incQty(stockMax: Int = 10) {
        val current = _ui.value
        val next = (current.qty + 1).coerceAtMost(stockMax)
        _ui.value = current.copy(
            qty = next,
            showShine = next != current.qty
        )
    }

    fun decQty() {
        val current = _ui.value
        _ui.value = current.copy(
            qty = (current.qty - 1).coerceAtLeast(1)
        )
    }

    fun consumeShine() {
        _ui.value = _ui.value.copy(showShine = false)
    }

    fun setZoom(show: Boolean) {
        _ui.value = _ui.value.copy(showZoom = show)
    }

    fun similar(): List<Product> =
        ui.value.product?.let { catalogVm.getSimilar(it) } ?: emptyList()

    /**
     * Construye un CartItem listo para mandarlo al CartViewModel.
     */
    fun toCartItem(): CartItem? {
        val state = ui.value
        val p = state.product ?: return null
        return CartItem(
            productId = p.id,
            name = p.nombre,
            image = p.imagen,
            size = state.selectedTamano,
            flavor = state.selectedSabor,
            quantity = state.qty,
            unitPrice = p.precio
        )
    }
}
