package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.ui.util.resIdFor

@Composable
fun TopCarrusel(
    items: List<Product>,
    onSeeMore: () -> Unit,
    onOpenProduct: (Product) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { items.size.coerceAtLeast(1) })
    val ctx = LocalContext.current

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) { page ->
        val p = items.getOrNull(page) ?: return@HorizontalPager
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(Modifier.fillMaxSize()) {
                val res = resIdFor(ctx, p.imagen)
                if (res != 0) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = p.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        p.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Button(onClick = { onOpenProduct(p) }) { Text("Ver detalle") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = onSeeMore) { Text("Ver más") }
                    }
                }
            }
        }
    }
}