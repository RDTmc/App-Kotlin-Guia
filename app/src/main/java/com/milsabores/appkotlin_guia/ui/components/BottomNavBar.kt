package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable


enum class BottomDest { HOME, MENU, CART, PROFILE }

@Composable
fun BottomNavBar(
    current: BottomDest,
    cartCount: Int,
    onSelect: (BottomDest) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == BottomDest.HOME,
            onClick = { onSelect(BottomDest.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = current == BottomDest.MENU,
            onClick = { onSelect(BottomDest.MENU) },
            icon = { Icon(Icons.Filled.ViewModule, contentDescription = "Menú") },
            label = { Text("Menú") }
        )
        NavigationBarItem(
            selected = current == BottomDest.CART,
            onClick = { onSelect(BottomDest.CART) },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                    }
                } else {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                }
            },
            label = { Text("Carrito") }
        )
        NavigationBarItem(
            selected = current == BottomDest.PROFILE,
            onClick = { onSelect(BottomDest.PROFILE) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}