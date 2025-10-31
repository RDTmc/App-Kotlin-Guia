package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.ViewModel
import com.milsabores.appkotlin_guia.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Int = 0,
    val iva: Int = 0,
    val shipping: Int = 0,
    val discount: Int = 0,
    val total: Int = 0
)

class CartViewModel : ViewModel() {

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui

    // 👇 inicial de prueba (puedes borrarlo cuando ya agregues desde ProductDetail)
    init {
        setItems(
            listOf(
                CartItem(
                    productId = "TC001",
                    name = "Torta Cuadrada de Chocolate",
                    image = "img/tc_chocolate.png",
                    size = "10 porciones",
                    flavor = "Chocolate",
                    quantity = 1,
                    unitPrice = 45000
                )
            )
        )
    }

    // ------------------ API pública ------------------

    /**
     * Sube en 1 la cantidad del producto dado, hasta 10.
     */
    fun inc(productId: String) {
        updateQty(productId, +1)
    }

    /**
     * Baja en 1 la cantidad del producto dado, hasta 1.
     */
    fun dec(productId: String) {
        updateQty(productId, -1)
    }

    /**
     * Elimina el producto del carrito.
     */
    fun remove(productId: String) {
        val filtered = _ui.value.items.filterNot { it.productId == productId }
        setItems(filtered)
    }

    /**
     * Limpia todo el carrito.
     */
    fun clear() {
        setItems(emptyList())
    }

    /**
     * Agrega un item nuevo o aumenta el existente (match por productId + size + flavor).
     */
    fun addOrIncrease(item: CartItem) {
        val current = _ui.value.items.toMutableList()
        val idx = current.indexOfFirst {
            it.productId == item.productId &&
                    it.size == item.size &&
                    it.flavor == item.flavor
        }
        if (idx >= 0) {
            val existing = current[idx]
            val newQty = (existing.quantity + item.quantity).coerceAtMost(10)
            current[idx] = existing.copy(quantity = newQty)
        } else {
            current += item
        }
        setItems(current)
    }

    /**
     * Actualiza la cantidad con un delta (+1 o -1).
     * La dejamos pública porque ya la estabas usando.
     */
    fun updateQty(productId: String, delta: Int) {
        val current = _ui.value.items
        val updated = current.mapNotNull { item ->
            if (item.productId == productId) {
                val newQty = (item.quantity + delta).coerceIn(1, 10)
                item.copy(quantity = newQty)
            } else {
                item
            }
        }
        setItems(updated)
    }

    // ------------------ interno ------------------

    private fun setItems(newItems: List<CartItem>) {
        _ui.value = recalc(newItems)
    }

    private fun recalc(items: List<CartItem>): CartUiState {
        val sub = items.sumOf { it.lineTotal }
        val iva = (sub * 0.19).toInt()
        val shipping = if (items.isEmpty()) 0 else 3900  // 👈 por ahora fijo
        val discount = 0                                // luego metemos FELICES50, 50 años, etc.
        val total = sub + iva + shipping - discount
        return CartUiState(
            items = items,
            subtotal = sub,
            iva = iva,
            shipping = shipping,
            discount = discount,
            total = total
        )
    }
}