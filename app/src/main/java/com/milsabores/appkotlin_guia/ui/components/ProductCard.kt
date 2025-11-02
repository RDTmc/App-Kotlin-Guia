package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.ui.util.resIdFor

@Composable
fun ProductCard(
    product: Product,
    onOpen: (Product) -> Unit
) {
    val ctx = LocalContext.current
    Card(
        onClick = { onOpen(product) },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            val res = resIdFor(ctx, product.imagen)
            if (res != 0) {
                Image(
                    painter = painterResource(res),
                    contentDescription = product.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Sin imagen") }
            }
            Column(Modifier.padding(12.dp)) {
                Text(product.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text("★ ${"%.1f".format(product.rating)}  ·  $${product.precio}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}