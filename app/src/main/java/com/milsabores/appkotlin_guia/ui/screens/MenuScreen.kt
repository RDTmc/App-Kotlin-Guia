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
import com.milsabores.appkotlin_guia.ui.components.FilterChipsRow
import com.milsabores.appkotlin_guia.ui.components.ProductCard
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    catalogVm: CatalogViewModel = viewModel()
) {
    // 👇 un solo estado unificado desde el VM
    val ui by catalogVm.ui.collectAsState()

    // 🔁 asegúrate de tener datos si se entra directo a esta pantalla
    LaunchedEffect(Unit) {
        if (catalogVm.ui.value.products.isEmpty()) {
            catalogVm.loadFromApi()
        }
    }

    // 🔎 búsqueda local
    var searchText by remember { mutableStateOf("") }

    // 🧮 aplicamos búsqueda sobre la lista ya filtrada por VM
    val displayedProducts = remember(ui.products, searchText) {
        if (searchText.isBlank()) ui.products
        else ui.products.filter { it.nombre.contains(searchText, ignoreCase = true) }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Chocolate,
        unfocusedBorderColor = Chocolate.copy(alpha = 0.5f),
        focusedLabelColor = Chocolate,
        cursorColor = Chocolate,
        unfocusedContainerColor = BlancoDos,
        focusedContainerColor = BlancoDos
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestro Catálogo", color = Chocolate) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoDos)
            )
        },
        containerColor = BlancoDos
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1) Barra de búsqueda
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar productos...", color = Chocolate.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Chocolate)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = textFieldColors
                )
            }

            // 2) Filtros (usa el filtro del VM)
            item {
                FilterChipsRow(
                    selected = ui.filter,
                    onSelected = catalogVm::setFilter
                )
            }

            // 3) Estado de carga / error
            item {
                when {
                    ui.isLoading -> {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    ui.error != null -> {
                        Text(
                            text = ui.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 4) Título de sección
            item {
                Text(
                    "Mostrando: ${ui.filter ?: "Todos los productos"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Chocolate,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 5) Lista de productos (clave = id)
            items(displayedProducts, key = { it.id }) { producto ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ProductCard(
                        product = producto,
                        onOpen = { p -> navController.navigate(AppRoute.Product.build(p.id)) }
                    )
                }
            }
        }
    }
}
