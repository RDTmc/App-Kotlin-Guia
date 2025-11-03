package com.milsabores.appkotlin_guia.ui.components

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


enum class BottomDest { HOME, MENU, CART, PROFILE }
@Composable
fun BottomNavBar(
    current: BottomDest,
    cartCount: Int,
    isLoggedIn: Boolean,
    onSelect: (BottomDest) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(60.dp),
        windowInsets = WindowInsets(0.dp)  // ← Elimina padding interno del sistema
    ) {
        NavigationBarItem(
            selected = current == BottomDest.HOME,
            onClick = { onSelect(BottomDest.HOME) },
            icon = { Icon(Icons.Filled.Roofing, contentDescription = "Inicio") }

        )
        NavigationBarItem(
            selected = current == BottomDest.MENU,
            onClick = { onSelect(BottomDest.MENU) },
            icon = { Icon(Icons.Filled.ViewModule, contentDescription = "Menú") }

        )
        NavigationBarItem(
            selected = current == BottomDest.CART,
            onClick = { onSelect(BottomDest.CART) },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text(if (cartCount > 9) "9+" else cartCount.toString()) } }) {
                        Icon(Icons.Filled.ShoppingBasket, contentDescription = "Carrito")
                    }
                } else {
                    Icon(Icons.Filled.ShoppingBasket, contentDescription = "Carrito")
                }
            }
        )
        NavigationBarItem(
            selected = current == BottomDest.PROFILE,
            onClick = { onSelect(BottomDest.PROFILE) },
            icon = {
                if (isLoggedIn) Icon(Icons.TwoTone.Person, null)
                else Icon(Icons.Filled.Person, null)
            },
            label = { Text(if (isLoggedIn) "Perfil" else "Invitado") }
        )
    }
}