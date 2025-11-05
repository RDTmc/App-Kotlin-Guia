package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.twotone.ShoppingBag
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.scale
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.RosaClaro
import androidx.compose.ui.text.font.FontWeight // Usaremos esto para el texto activo

enum class BottomDest { HOME, MENU, CART, PROFILE }

@Composable
fun BottomNavBar(
    current: BottomDest,
    cartCount: Int,
    isLoggedIn: Boolean,
    onSelect: (BottomDest) -> Unit
) {
    // Definición de colores de la NavigationBar (Barra de Navegación)
    NavigationBar(
        modifier = Modifier.height(60.dp),
        windowInsets = WindowInsets(0.dp),
        containerColor = Chocolate // 💡 Fondo oscuro de la barra
    ) {
        // Itera sobre las posibles destinaciones
        BottomDest.entries.forEach { destination ->
            val isSelected = current == destination

            // 💡 Animación de escala: 1.2f si está seleccionado, 1.0f si no.
            val scale: Float by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1.0f,
                label = "iconScaleAnimation"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(destination) },
                icon = {
                    Box(modifier = Modifier.scale(scale)) { // 💡 Aplicamos la escala al Box contenedor del icono
                        val icon = when (destination) {
                            BottomDest.HOME -> Icons.Filled.Roofing
                            BottomDest.MENU -> Icons.Filled.ViewModule
                            BottomDest.CART -> Icons.Filled.ShoppingBasket
                            BottomDest.PROFILE -> if (isLoggedIn) Icons.TwoTone.Person else Icons.Filled.Person
                        }

                        if (destination == BottomDest.CART && cartCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = RosaClaro, // Fondo del badge
                                        contentColor = Chocolate     // Texto del badge
                                    ) {
                                        Text(if (cartCount > 9) "9+" else cartCount.toString())
                                    }
                                }
                            ) {
                                Icon(icon, contentDescription = "Carrito")
                            }
                        } else {
                            Icon(icon, contentDescription = destination.name)
                        }
                    }
                },
                label = {
                    Text(
                        when (destination) {
                            BottomDest.HOME -> "Inicio"
                            BottomDest.MENU -> "Menú"
                            BottomDest.CART -> "Carrito"
                            BottomDest.PROFILE -> if (isLoggedIn) "Perfil" else "Invitado"
                        },
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                // 💡 Aplicación de colores de los ítems de navegación
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RosaClaro,     // Ícono activo
                    selectedTextColor = RosaClaro,     // Texto activo
                    indicatorColor = Chocolate,        // El indicador debe ser igual al fondo o transparente.
                    unselectedIconColor = RosaClaro.copy(alpha = 0.6f), // Ícono inactivo (más tenue)
                    unselectedTextColor = RosaClaro.copy(alpha = 0.6f)  // Texto inactivo (más tenue)
                )
            )
        }
    }
}