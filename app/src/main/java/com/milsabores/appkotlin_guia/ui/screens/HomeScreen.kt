package com.milsabores.appkotlin_guia.ui.screens

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.BottomDest
import com.milsabores.appkotlin_guia.ui.components.BottomNavBar
import com.milsabores.appkotlin_guia.ui.components.ProductCard
import com.milsabores.appkotlin_guia.ui.components.TopCarrusel
import com.milsabores.appkotlin_guia.ui.components.FilterChipsRow
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.Pacifico
import com.milsabores.appkotlin_guia.ui.theme.RosaText
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel
import com.milsabores.appkotlin_guia.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavController,
    cartVm: CartViewModel,
    catalogVm: CatalogViewModel,
    isLoggedIn: Boolean

) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val featured by catalogVm.featured.collectAsState()
    val filter by catalogVm.filter.collectAsState()
    val products by catalogVm.products.collectAsState()
    val cartUi by cartVm.ui.collectAsState()
    val cartCount = cartUi.items.sumOf { it.quantity }

    var bottomSel by remember { mutableStateOf(BottomDest.HOME) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Ir al Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(AppRoute.Profile)
                    }
                )

            }
        }
    )   {
        Scaffold(

            containerColor = BlancoDos,
            topBar = {
                // 💡 CAMBIO A CENTERED TOP APP BAR
                CenterAlignedTopAppBar(
                    title = {
                        // 💡 Título Centrado con Fuente Pacifico
                        Text(
                            "Mil Sabores",
                            style = TextStyle(
                                fontFamily = Pacifico,
                                fontSize = 32.sp, // Fuente más grande para destacar
                                color = Chocolate   // Letras en Rosa Suave
                            ),
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors( // Usamos los defaults centrados
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = RosaText,
                        navigationIconContentColor = Chocolate
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    // 💡 Ícono de Acción (Ejemplo: Ícono de Búsqueda o Carrito)
                    actions = {
                        IconButton(onClick = { /* Abre la búsqueda en pantalla completa */ }) {
                            Icon(Icons.Filled.Cake, contentDescription = "Torta",
                                tint = Chocolate)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(
                    current = bottomSel,
                    cartCount = cartCount,
                    isLoggedIn = isLoggedIn,
                    onSelect = { dest ->
                        bottomSel = dest
                        when (dest) {
                            BottomDest.HOME -> Unit
                            BottomDest.MENU -> viewModel.navigateTo(AppRoute.Resumen)
                            BottomDest.CART -> navController.navigate(AppRoute.Cart.route)
                            BottomDest.PROFILE -> viewModel.navigateTo(AppRoute.Profile)
                        }
                    }
                )
            }
          ) { innerPadding ->
              Column(
                  modifier = Modifier
                      .padding(
                          top = innerPadding.calculateTopPadding(),
                          bottom = 0.dp  // ← Elimina el padding bottom automático
                      )
                      .fillMaxSize()
              ) {


                // Carrusel
                TopCarrusel(
                    items = featured.take(3),
                    onSeeMore = { catalogVm.setFilter(null) }, // "Ver todos" rápido
                    onOpenProduct = { p -> openProduct(navController, p) }
                )

                Spacer(Modifier.height(8.dp))

                // Filtros
                FilterChipsRow(
                    selected = filter,
                    onSelected = { f -> catalogVm.setFilter(f) }
                )

                // Grilla
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 20.dp,end = 20.dp,
                        top = 20.dp,bottom = 85.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { p ->
                        ProductCard(product = p, onOpen = { openProduct(navController, it) })
                    }
                }
            }
        }
    }
}

private fun openProduct(nav: NavController, p: Product) {
    nav.navigate(AppRoute.Product.build(p.id))
}