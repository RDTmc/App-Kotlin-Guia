package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.model.OrderEntity
import com.milsabores.appkotlin_guia.repository.AppDataBase
import com.milsabores.appkotlin_guia.repository.OrderRepository
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen() {
    val context = LocalContext.current

    // Creamos el repo una sola vez para esta pantalla
    val orderRepo = remember {
        val db = AppDataBase.getInstance(context)
        OrderRepository(db.orderDao())
    }

    val ordersFlow: Flow<List<OrderEntity>> = remember { orderRepo.observeOrders() }
    val orders by ordersFlow.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = BlancoDos,
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (orders.isEmpty()) {
                Text(
                    text = "Aún no tienes pedidos registrados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Chocolate
                )
            } else {
                Text(
                    text = "Historial de pedidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Chocolate
                )
                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderItemCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemCard(order: OrderEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Pedido #${order.id}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            // createdAt es un Long (epoch millis). Para MVP lo mostramos “en crudo”;
            // si quieres lo formateamos con SimpleDateFormat en otra iteración.
            Text(
                text = "Creado: ${order.createdAt}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Dirección: ${order.address}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!order.date.isNullOrBlank() || !order.time.isNullOrBlank()) {
                Text(
                    text = "Entrega: ${(order.date ?: "")} ${(order.time ?: "")}".trim(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Pago: ${order.payment}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Subtotal: \$${order.subtotal}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "IVA: \$${order.iva}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Envío: \$${order.shipping}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (order.discount != 0) {
                Text(
                    text = "Descuento: -\$${order.discount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Total: \$${order.total}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Chocolate
            )
        }
    }
}
