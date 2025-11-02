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
    private val repo: CartRepository? = null,   // null en MVP, la inyectamos en MainActivity
            private val orderRepo: OrderRepository? = null
) : ViewModel() {

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui

    init {
        // si tenemos repo, escuchamos Room
        repo?.observeCart()
            ?.onEach { entities ->
                val items = entities.map { it.toDomain() }
                if (!sameItemList(items, _ui.value.items)) {
                    _ui.value = recalc(items)
                }
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
        persistAll(updated)
    }

    fun inc(productId: String) = updateQty(productId, +1)
    fun dec(productId: String) = updateQty(productId, -1)

    fun remove(productId: String) {
        val filtered = _ui.value.items.filterNot { it.productId == productId }
        persistAll(filtered)
    }

    fun clear() {
        persistAll(emptyList())
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
        persistAll(current)
    }

    private fun persistAll(items: List<CartItem>) {
        val r = repo ?: run {
            // sin repo, actualizamos UI localmente
            setItems(items)
            return
        }

        viewModelScope.launch {
            try {
                // si el repo tuviera una API replaceAll sería mejor.
                // Ejecutamos clear() seguido de inserts secuenciales, pero esperamos a que termine todo.
                r.clear()
                items.forEach { ci ->
                    r.add(ci.toEntity())
                }
                // Sólo después de que la persistencia terminó, actualizamos UI local (optimista controlado)
                // Nota: si Room emite la misma lista desde observeCart, el observer se encargará de la actualización.
                // Aquí hacemos la actualización local para tener respuesta inmediata en caso de que el repo no emita.
                val emitted = r.observeCart()?.let { flow ->
                    // no bloqueante: preferimos actualizar local; la fuente de verdad seguirá siendo Room
                    null
                }
                // Actualizar UI local para reflejar el estado final de la operación (reduce flicker al evitar emitir antes)
                setItems(items)
            } catch (e: Exception) {
                // Si hay error en persistencia, podríamos notificar al UI (no implementado aquí)
            }
        }
    }

    // Finalizar compra
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
            repo ?.clear()
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

    /**
     * Compara dos listas de CartItem por su contenido relevante (id, size, flavor, quantity).
     * Evita asignar _ui.value cuando no hay cambios reales.
     */
    private fun sameItemList(a: List<CartItem>, b: List<CartItem>): Boolean {
        if (a.size != b.size) return false
        for (i in a.indices) {
            val ai = a[i]
            val bi = b[i]
            if (ai.productId != bi.productId) return false
            if (ai.size != bi.size) return false
            if (ai.flavor != bi.flavor) return false
            if (ai.quantity != bi.quantity) return false
            if (ai.unitPrice != bi.unitPrice) return false
            // no comparamos name/image por ahora; si cambian con frecuencia pueden provocar recomposiciones
        }
        return true
    }
}