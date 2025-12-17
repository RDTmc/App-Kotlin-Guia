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

// >>> IMPORTS NUEVOS para ms-orders
import com.milsabores.appkotlin_guia.data.remote.CreateOrderItemDto
import com.milsabores.appkotlin_guia.data.remote.CreateOrderRequestDto
import com.milsabores.appkotlin_guia.data.remote.OrdersRemoteRepository

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Int = 0,
    val iva: Int = 0,
    val shipping: Int = 0,
    val discount: Int = 0,
    val total: Int = 0
)

class CartViewModel(
    private val repo: CartRepository? = null,          // Room (carrito local)
    private val orderRepo: OrderRepository? = null,    // Room (historial de órdenes)
    private val ordersRemote: OrdersRemoteRepository = OrdersRemoteRepository() // ms-orders
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
                r.clear()
                items.forEach { ci ->
                    r.add(ci.toEntity())
                }
                setItems(items)
            } catch (e: Exception) {
                // aquí podrías notificar error a la UI si lo necesitas
            }
        }
    }

    /**
     * Finalizar compra contra ms-orders.
     *
     * @param token JWT emitido por ms-usuarios (SIN "Bearer ")
     * @param address Dirección de envío / retiro
     * @param date Fecha elegida (opcional, sólo para mostrar en Room)
     * @param time Hora elegida (opcional, sólo para mostrar en Room)
     * @param payment Método de pago ("CARD", "TRANSFER", "CASH", etc.)
     * @param discountCode Código de promoción ingresado por el usuario (ej: "FELICES50")
     * @param onResult Callback al terminar: (éxito, mensaje para mostrar)
     */
    fun placeOrderRemote(
        token: String,
        address: String,
        date: String?,
        time: String?,
        payment: String,
        discountCode: String? = null,
        onResult: (Boolean, String?) -> Unit
    ) {
        val snapshot = _ui.value

        if (snapshot.items.isEmpty()) {
            onResult(false, "El carrito está vacío")
            return
        }

        // Construir el payload que espera ms-orders
        val itemsDto = snapshot.items.map { ci ->
            CreateOrderItemDto(
                productId = ci.productId,
                productName = ci.name,
                image = ci.image,
                unitPrice = ci.unitPrice,
                quantity = ci.quantity,
                size = ci.size,
                flavor = ci.flavor
            )
        }

        val request = CreateOrderRequestDto(
            paymentMethod = payment,
            shippingAddress = address,
            items = itemsDto,
            discountCode = discountCode
        )

        viewModelScope.launch {
            try {
                // 1) Crear orden en ms-orders (remoto, con JWT)
                val orderResponse = ordersRemote.createOrder(
                    token = token,
                    request = request,
                    userId = null   // ms-orders tomará userId desde el JWT (sub)
                )

                // 2) Guardar un registro local en Room (historial)
                orderRepo?.save(
                    OrderEntity(
                        createdAt = System.currentTimeMillis(),
                        address = address,
                        date = date,
                        time = time,
                        payment = payment,
                        subtotal = snapshot.subtotal,
                        iva = snapshot.iva,
                        shipping = snapshot.shipping,
                        discount = snapshot.discount,
                        total = snapshot.total
                    )
                )

                // 3) Limpiar carrito local (Room + UI)
                repo?.clear()
                _ui.value = CartUiState()

                val msg = if (!orderResponse.id.isNullOrBlank()) {
                    "Orden creada correctamente (ID: ${orderResponse.id})"
                } else {
                    "Orden creada correctamente"
                }
                onResult(true, msg)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Error al crear la orden en el servidor")
            }
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
        }
        return true
    }
}
