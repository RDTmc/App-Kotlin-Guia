package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.FilterChipsRow // 💡 Importación del componente de filtros
import com.milsabores.appkotlin_guia.ui.components.ProductCard // 💡 Importación del componente de tarjeta
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel // 💡 Importación del ViewModel del catálogo
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos

// 💡 Nota: Cambiamos el nombre de la función de composable de ResumenScreen a MenuScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    catalogVm: CatalogViewModel = viewModel() // 💡 Recibimos/Creamos el CatalogViewModel
) {
    // 💡 Leer el estado del filtro y de los productos
    val currentFilter by catalogVm.filter.collectAsState()
    val products by catalogVm.products.collectAsState()

    // 💡 Estado local para la búsqueda (Search Bar)
    var searchText by remember { mutableStateOf("") }

    // 💡 Creamos la lista final a mostrar aplicando la búsqueda local
    val displayedProducts = remember(products, searchText) {
        if (searchText.isBlank()) {
            products
        } else {
            products.filter {
                it.nombre.contains(searchText, ignoreCase = true)
            }
        }
    }

    // Colores para el TextField (usamos el estilo estable)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Chocolate,
        unfocusedBorderColor = Chocolate.copy(alpha = 0.5f),
        focusedLabelColor = Chocolate,
        cursorColor = Chocolate,
        unfocusedContainerColor = BlancoDos,
        focusedContainerColor = BlancoDos
    )

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Nuestro Catálogo", color = Chocolate) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoDos)
        )},
        containerColor = BlancoDos
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para la BottomNavBar
        ) {

            // 1. BARRA DE BÚSQUEDA
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar productos...", color = Chocolate.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Chocolate) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = textFieldColors // 💡 Aplicamos los colores de marca
                )
            }

            // 2. FILTROS DE CATEGORÍAS (Componente reutilizable FilterChipsRow)
            item {
                FilterChipsRow(
                    selected = currentFilter,
                    onSelected = catalogVm::setFilter
                )
            }

            // 3. TÍTULO DE SECCIÓN
            item {
                Text(
                    "Mostrando: ${currentFilter ?: "Todos los productos"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Chocolate,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 4. LISTA DE PRODUCTOS (usando el ProductCard)
            items(displayedProducts, key = { it.id }) { producto ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ProductCard(
                        product = producto,
                        onOpen = { p ->
                            // Ejemplo de navegación a detalle:
                            navController.navigate(AppRoute.Product.build(p.id))
                        }
                    )
                }
            }
        }
    }
}

