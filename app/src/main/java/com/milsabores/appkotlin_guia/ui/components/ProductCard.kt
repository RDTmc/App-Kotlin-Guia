package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanToolAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.ui.util.resIdFor
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.blur

@Composable
fun ProductCard(
    product: Product,
    onOpen: (Product) -> Unit
) {
    val ctx = LocalContext.current
    val imageRes = remember(product.imagen) { resIdFor(ctx, product.imagen ?: "") }
    // proporción ancho:alto para la tarjeta (ajusta si prefieres otra)
    val cardAspect = 3f / 4f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(cardAspect),
        shape = RoundedCornerShape(12.dp),
        // usar color surface explícito (no transparente) para evitar que se vea el fondo crema por detrás
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        onClick = { onOpen(product) }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Imagen (aprox 58% de la altura)
            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .fillMaxWidth()
            ) {
                if (imageRes != 0) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = product.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin imagen", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Overlay degradado sutil en la parte inferior de la imagen para legibilidad
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.20f)),
                                startY = 0f
                            )
                        )
                )
            }

            // Zona de texto y acción (aprox 42% restante)
            Column(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.1f),
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .blur(radius = 1.dp)
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column {
                    Text(
                        text = product.categoria ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = product.nombre,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                    )

                    val isShortName = (product.nombre.length < 23) // Ajusta este número según tus datos reales

                    if (isShortName) {
                        // Altura aproximada de una línea de texto bodyLarge + padding.
                        // Esto reserva el espacio de la segunda línea que el nombre no usó.
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween // 👈 Usamos SpaceBetween para empujar el ícono a la derecha
                    ) {
                        // 1. Texto de Rating y Precio
                        Text(
                            text = buildString {
                                if (product.rating != null) {
                                    append("★ ${"%.1f".format(product.rating)}  ·  ")
                                }
                                append("$${product.precio}")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // 2. Ícono "Ver Más" (Expand Circle Right)
                        IconButton(
                            onClick = { onOpen(product) },
                            // 💡 AJUSTE: Reducimos el tamaño del IconButton para que quepa mejor en línea con el texto
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = "Ver detalles de ${product.nombre}",
                                tint = Color(0xFF573123)
                            )
                        }
                    }
                }
            }
        }
    }
}