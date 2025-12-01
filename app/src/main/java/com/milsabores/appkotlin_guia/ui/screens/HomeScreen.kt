package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.BottomDest
import com.milsabores.appkotlin_guia.ui.components.BottomNavBar
import com.milsabores.appkotlin_guia.ui.components.FilterChipsRow
import com.milsabores.appkotlin_guia.ui.components.ProductCard
import com.milsabores.appkotlin_guia.ui.components.TopCarrusel
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

    // Nuevo: un solo estado unificado desde el VM
    val ui by catalogVm.ui.collectAsState()

    // Carrito
    val cartUi by cartVm.ui.collectAsState()
    val cartCount = cartUi.items.sumOf { it.quantity }

    // Carga inicial si no hay productos aún
    LaunchedEffect(Unit) {
        if (catalogVm.ui.value.products.isEmpty()) {
            catalogVm.loadFromApi()
        }
    }

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
    ) {
        Scaffold(
            containerColor = BlancoDos,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Mil Sabores",
                            style = TextStyle(
                                fontFamily = Pacifico,
                                fontSize = 32.sp,
                                color = Chocolate
                            ),
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = centerAlignedTopAppBarColors(
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
                    actions = {
                        IconButton(onClick = { /* abrir búsqueda */ }) {
                            Icon(Icons.Filled.Cake, contentDescription = "Torta", tint = Chocolate)
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
                        bottom = 0.dp
                    )
                    .fillMaxSize()
            ) {
                // Carrusel (usa ui.featured)
                if (ui.featured.isNotEmpty()) {
                    TopCarrusel(
                        items = ui.featured.take(3),
                        onSeeMore = { catalogVm.setFilter(null) },
                        onOpenProduct = { p -> openProduct(navController, p) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Filtros (usa ui.filter)
                FilterChipsRow(
                    selected = ui.filter,
                    onSelected = { f -> catalogVm.setFilter(f) }
                )

                // Estado de carga / error (opcional visual)
                when {
                    ui.isLoading -> {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    ui.error != null -> {
                        Text(
                            text = ui.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }

                // Grilla (usa ui.products)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 20.dp, end = 20.dp,
                        top = 20.dp, bottom = 85.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ui.products) { p ->
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
