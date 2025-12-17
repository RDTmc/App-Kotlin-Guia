package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.OrderEntity
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de historial de pedidos.
 *
 * Recibe:
 * - navController para navegación (volver atrás o a otras pantallas)
 * - ordersFlow: Flow<List<OrderEntity>> que proviene de Room (orderDao.getAll())
 *
 * En la siguiente iteración solo habrá que “conectar” esta pantalla en MainActivity
 * usando:
 *   val db = AppDataBase.getInstance(context)
 *   val ordersFlow = remember { db.orderDao().getAll() }
 *   composable(AppRoute.Estado.route) {
 *       EstadoScreen(navController, ordersFlow)
 *   }
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadoScreen(
    navController: NavController,
    ordersFlow: Flow<List<OrderEntity>>
) {
    val orders by ordersFlow.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = BlancoDos,
        topBar = {
            TopAppBar(
                title = { Text("Historial de pedidos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        if (orders.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes pedidos registrados.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Lista de pedidos
            LazyColumn(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Pedido #${order.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Chocolate
            )

            Text(
                text = "Fecha: ${order.createdAt.humanDate()}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(4.dp))

            Text("Dirección: ${order.address}")
            if (!order.date.isNullOrBlank() || !order.time.isNullOrBlank()) {
                Text(
                    text = buildString {
                        append("Entrega: ")
                        if (!order.date.isNullOrBlank()) append(order.date)
                        if (!order.time.isNullOrBlank()) {
                            if (!order.date.isNullOrBlank()) append(" ")
                            append(order.time)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text("Método de pago: ${order.payment}")

            Spacer(Modifier.height(6.dp))

            Text("Subtotal: ${formatCLP(order.subtotal)}")
            Text("IVA (19%): ${formatCLP(order.iva)}")
            Text("Envío: ${formatCLP(order.shipping)}")
            if (order.discount > 0) {
                Text("Descuento: -${formatCLP(order.discount)}")
            }

            Divider(Modifier.padding(vertical = 6.dp))

            Text(
                text = "Total: ${formatCLP(order.total)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Helpers ---

private fun Long.humanDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

private fun formatCLP(value: Int): String =
    "$" + "%,d".format(value).replace(',', '.')
