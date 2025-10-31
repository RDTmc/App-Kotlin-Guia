package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milsabores.appkotlin_guia.model.CartItem
import com.milsabores.appkotlin_guia.model.CartEntity
import com.milsabores.appkotlin_guia.model.OrderEntity
import com.milsabores.appkotlin_guia.repository.CartRepository
import com.milsabores.appkotlin_guia.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Int = 0,
    val iva: Int = 0,
    val shipping: Int = 0,
    val discount: Int = 0,
    val total: Int = 0
)

class CartViewModel(
    private val repo: CartRepository? = null,   // 👈 null en MVP, la inyectamos en MainActivity
            private val orderRepo: OrderRepository? = null
) : ViewModel() {

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui

    init {
        // si tenemos repo, escuchamos Room
        repo?.observeCart()
            ?.onEach { entities ->
                val items = entities.map { it.toDomain() }
                _ui.value = recalc(items)
            }
            ?.launchIn(viewModelScope)
    }

    private fun recalc(items: List<CartItem>): CartUiState {
        val sub = items.sumOf { it.lineTotal }
        val iva = (sub * 0.19).toInt()
        val shipping = if (items.isEmpty()) 0 else 3900
        val discount = 0
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

    private fun setItems(newItems: List<CartItem>) {
        _ui.value = recalc(newItems)
    }

    fun updateQty(productId: String, delta: Int) {
        val current = _ui.value.items
        val updated = current.mapNotNull { item ->
            if (item.productId == productId) {
                val newQty = (item.quantity + delta).coerceIn(1, 10)
                item.copy(quantity = newQty)
            } else item
        }
        setItems(updated)
        // guardar en Room
        persistAll(updated)
    }

    fun inc(productId: String) = updateQty(productId, +1)
    fun dec(productId: String) = updateQty(productId, -1)

    fun remove(productId: String) {
        val filtered = _ui.value.items.filterNot { it.productId == productId }
        setItems(filtered)
        persistAll(filtered)
    }

    fun clear() {
        setItems(emptyList())
        viewModelScope.launch {
            repo?.clear()
        }
    }

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
        persistAll(current)
    }

    private fun persistAll(items: List<CartItem>) {
        val r = repo ?: return
        viewModelScope.launch {
            r.clear()
            items.forEach { ci ->
                r.add(ci.toEntity())
            }
        }
    }

    // 👇 NUEVO: finalizar compra
    fun placeOrder(
        address: String,
        date: String?,
        time: String?,
        payment: String,
        // descuentos ya aplicados en UI → le pasamos shipping final y discount final
        shipping: Int,
        discount: Int
    ) {
        val snapshot = _ui.value
        viewModelScope.launch {
            // 1. guardar orden
            orderRepo?.save(
                OrderEntity(
                    createdAt = System.currentTimeMillis(),
                    address = address,
                    date = date,
                    time = time,
                    payment = payment,
                    subtotal = snapshot.subtotal,
                    iva = snapshot.iva,
                    shipping = shipping,
                    discount = discount,
                    total = snapshot.subtotal + snapshot.iva + shipping - discount
                )
            )
            // 2. limpiar carrito
            cartRepo ?.clear()
            _ui.value = CartUiState() // limpiar UI
        }
    }

    // mapeos
    private fun CartEntity.toDomain() = CartItem(
        productId = productId,
        name = name,
        image = image,
        size = size,
        flavor = flavor,
        quantity = quantity,
        unitPrice = unitPrice
    )

    private fun CartItem.toEntity() = CartEntity(
        productId = productId,
        name = name,
        image = image,
        size = size,
        flavor = flavor,
        quantity = quantity,
        unitPrice = unitPrice
    )
}