package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.ui.theme.BlancoMarfil
import com.milsabores.appkotlin_guia.ui.theme.RosaFuerteDos
import com.milsabores.appkotlin_guia.ui.util.resIdFor

@Composable
fun TopCarrusel(
    items: List<Product>,
    onSeeMore: () -> Unit,
    onOpenProduct: (Product) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { items.size.coerceAtLeast(1) })
    val ctx = LocalContext.current

    val shadowStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Black,
        color = Color.White, // Texto en blanco para alto contraste
        fontSize = 20.sp,
        shadow = androidx.compose.ui.graphics.Shadow(
            color = Color.Black.copy(alpha = 0.8f),
            offset = Offset(2f, 2f),
            blurRadius = 4f
        )
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) { page ->
        val p = items.getOrNull(page) ?: return@HorizontalPager

        val isRemoteImage = remember(p.imagen) {
            p.imagen.startsWith("http", ignoreCase = true)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 3.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoMarfil)
        ) {
            Box(Modifier.fillMaxSize()) {
                when {
                    isRemoteImage -> {
                        AsyncImage(
                            model = ImageRequest.Builder(ctx)
                                .data(p.imagen)
                                .crossfade(true)
                                .build(),
                            contentDescription = p.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        val res = resIdFor(ctx, p.imagen)
                        if (res != 0) {
                            Image(
                                painter = painterResource(res),
                                contentDescription = p.nombre,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        p.nombre,
                        style = shadowStyle,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Button(
                            onClick = { onOpenProduct(p) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RosaFuerteDos,
                                contentColor = BlancoMarfil
                            )
                        ) {
                            Text("Ver detalle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
