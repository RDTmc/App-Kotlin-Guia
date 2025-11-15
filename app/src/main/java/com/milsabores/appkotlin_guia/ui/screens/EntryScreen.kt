package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.ui.theme.Cafe
import com.milsabores.appkotlin_guia.ui.theme.CafeOsc
import com.milsabores.appkotlin_guia.ui.theme.CafeText
import com.milsabores.appkotlin_guia.ui.theme.Rosa
import com.milsabores.appkotlin_guia.ui.theme.RosaText
import com.milsabores.appkotlin_guia.ui.theme.Vainilla
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel

@Composable
fun EntryScreen(
    isLogged: Boolean,
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    // VM con valor por defecto para no romper llamadas existentes
    catalogVm: CatalogViewModel = viewModel()
) {
    // Leemos TODO el estado del catálogo desde un único StateFlow
    val ui by catalogVm.ui.collectAsState()

    // Control para abrir/cerrar el modal de demo API
    var showApiDemo by remember { mutableStateOf(false) }

    // Carga inicial desde la API si aún no hay productos
    LaunchedEffect(Unit) {
        if (catalogVm.ui.value.products.isEmpty()) {
            catalogVm.loadFromApi()
        }
    }

    if (isLogged) {
        // Si el usuario tiene sesión → a Home
        LaunchedEffect(Unit) { onGuestClick() }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAFAFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // (Opcional) cartel de carga/errores — no cambia tu UI, solo informa
            if (ui.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(bottom = 24.dp))
            }
            ui.error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Text(
                buildAnnotatedString {
                    append("Ingresa como invitado o ")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("inicia sesión para más ventajas.")
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Spacer(Modifier.height(28.dp))

            ElevatedButton(
                onClick = onGuestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Cafe,
                    contentColor = CafeText
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.PersonPin, contentDescription = "Invitado", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Invitado", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Rosa,
                    contentColor = RosaText
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Iniciar sesión", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Iniciar sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Vainilla,
                    contentColor = CafeOsc
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.AppRegistration, contentDescription = "Crear cuenta", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Botón para mostrar la DEMO de consumo de API REST (guía)
            TextButton(onClick = { showApiDemo = true }) {
                Text(
                    text = "Ver demo consumo API REST (Productos)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 🔹 Modal que muestra productos traídos desde la API
        if (showApiDemo) {
            AlertDialog(
                onDismissRequest = { showApiDemo = false },
                confirmButton = {
                    TextButton(onClick = { showApiDemo = false }) {
                        Text("Cerrar")
                    }
                },
                title = {
                    Text("Consumo API REST - Catálogo")
                },
                text = {
                    if (ui.isLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cargando productos desde el backend…")
                        }
                    } else if (ui.error != null) {
                        Text(
                            text = "Error al cargar catálogo:\n${ui.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                        ){
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ){
                               item {
                                   Text(
                                       text = "Mostrando ${ui.products.size} productos obtenidos desde la API de Mil Sabores:",
                                       style = MaterialTheme.typography.bodySmall,
                                       modifier = Modifier.padding(bottom = 8.dp)
                                   )
                                   Divider()
                               }

                                items(ui.products) { product ->
                                    DemoProductRow(product)
                                    Divider()
                                }

                                if (ui.products.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No se encontraron productos.",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun DemoProductRow(product: Product) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = product.nombre,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = "Precio: $${product.precio}",
            style = MaterialTheme.typography.bodySmall
        )
        if (product.descripcion.isNotBlank()) {
            Text(
                text = product.descripcion,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
