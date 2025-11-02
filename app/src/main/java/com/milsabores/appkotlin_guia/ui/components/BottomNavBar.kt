package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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



enum class BottomDest { HOME, MENU, CART, PROFILE }
@Composable
fun BottomNavBar(
    current: BottomDest,
    cartCount: Int,
    onSelect: (BottomDest) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(75.dp),
        windowInsets = WindowInsets(0.dp)  // ← Elimina padding interno del sistema
    ) {
        NavigationBarItem(
            selected = current == BottomDest.HOME,
            onClick = { onSelect(BottomDest.HOME) },
            icon = { Icon(Icons.TwoTone.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = current == BottomDest.MENU,
            onClick = { onSelect(BottomDest.MENU) },
            icon = { Icon(Icons.TwoTone.Menu, contentDescription = "Menú") },
            label = { Text("Menú") }
        )
        NavigationBarItem(
            selected = current == BottomDest.CART,
            onClick = { onSelect(BottomDest.CART) },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text(if (cartCount > 9) "9+" else cartCount.toString()) } }) {
                        Icon(Icons.TwoTone.ShoppingCart, contentDescription = "Carrito")
                    }
                } else {
                    Icon(Icons.TwoTone.ShoppingCart, contentDescription = "Carrito")
                }
            },
            label = { Text("Carrito") }
        )
        NavigationBarItem(
            selected = current == BottomDest.PROFILE,
            onClick = { onSelect(BottomDest.PROFILE) },
            icon = { Icon(Icons.TwoTone.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}