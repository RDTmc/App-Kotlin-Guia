package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.Product
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.ProductCard
import com.milsabores.appkotlin_guia.ui.util.resIdFor
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel
import com.milsabores.appkotlin_guia.viewmodel.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    cartVm: CartViewModel,
    catalogVm: CatalogViewModel
) {
    // VM con SavedStateHandle (Compose lo provee en esta destination)
    val vm: ProductDetailViewModel = viewModel()
    val catalogVm: CatalogViewModel = viewModel()
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current

    // Cargar una sola vez cuando recibimos el id
    LaunchedEffect(productId) {
        vm.load(productId, catalogVm)
    }

    val p = ui.product

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(p?.nombre ?: "Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Contador
                    OutlinedButton(onClick = { vm.decQty() }) { Text("−") }
                    // Micro-animación "brillo"
                    val target = if (ui.showShine) 1f else 0f
                    val alpha by animateFloatAsState(
                        targetValue = target,
                        animationSpec = tween(450, easing = FastOutLinearInEasing),
                        finishedListener = { vm.consumeShine() }
                    )
                    Text(" ${ui.qty} ", style = MaterialTheme.typography.titleLarge, modifier = Modifier.alpha(1f))
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                            .alpha(alpha)
                            .background(Color.Yellow.copy(0.6f), shape = MaterialTheme.shapes.small)
                    )
                    OutlinedButton(onClick = { vm.incQty() }) { Text("+") }

                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            val product = ui.product ?: return@Button
                            val item = com.milsabores.appkotlin_guia.model.CartItem(
                                productId = product.id,
                                name = product.nombre,
                                image = product.imagen,
                                size = ui.selectedTamano,
                                flavor = ui.selectedSabor,
                                quantity = ui.qty,
                                unitPrice = product.precio
                            )
                            navController.navigate(AppRoute.Cart.route)
                        },
                        modifier = Modifier.height(48.dp)
                    ) { Text("Agregar al carrito")
                    }
                }
            }
        }
    ) { pv ->
        if (p == null) {
            Box(Modifier.fillMaxSize().padding(pv), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen grande + zoom (tap para abrir modal zoom)
            val res = resIdFor(ctx, p.imagen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                if (res != 0) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = p.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clickable { vm.setZoom(true) },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sin imagen")
                }
            }

            // Título, rating, precio
            Text(p.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("★★★★☆ (128 reseñas)")
            Text("$${p.precio}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)

            // Selector tamaño
            Text("Tamaño", fontWeight = FontWeight.SemiBold)
            FlowRowWrap {
                ui.tamanos.forEach { t ->
                    FilterChip(selected = ui.selectedTamano == t, onClick = { vm.setTamano(t) }, label = { Text(t) })
                }
            }

            // Selector sabor
            Text("Sabor", fontWeight = FontWeight.SemiBold)
            FlowRowWrap {
                ui.sabores.forEach { s ->
                    FilterChip(selected = ui.selectedSabor == s, onClick = { vm.setSabor(s) }, label = { Text(s) })
                }
            }

            // Mensaje con contador (≤ 30)
            OutlinedTextField(
                value = ui.mensaje,
                onValueChange = vm::setMensaje,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mensaje en la torta (máx 30)") },
                supportingText = { Text("${ui.mensajeCount}/30") },
                isError = ui.mensajeError != null
            )
            ui.mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Similares
            Text("Productos similares", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(vm.similar()) { sp: Product ->
                    Box(Modifier.width(180.dp)) {
                        ProductCard(product = sp, onOpen = { /* nav a product/{id} */ })
                    }
                }
            }
        }
    }

    // Modal de ZOOM - CORREGIDO
    if (ui.showZoom) {
        val currentProduct = ui.product
        if (currentProduct != null) {
            FullscreenZoomDialog(
                product = currentProduct,
                onDismiss = { vm.setZoom(false) }
            )
        }
    }
}

@Composable
private fun FlowRowWrap(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun FullscreenZoomDialog(product: Product, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val res = resIdFor(ctx, product.imagen)
    var scale by remember { mutableFloatStateOf(1f) }
    val state = remember {
        TransformableState { zoomChange, _, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 3f)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (res != 0) {
                Image(
                    painter = painterResource(res),
                    contentDescription = product.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .transformable(state),
                    contentScale = ContentScale.Fit
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }
    }
}