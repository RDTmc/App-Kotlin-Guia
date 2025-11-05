package com.milsabores.appkotlin_guia.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.EstadoDataStore
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.ImagenInteligente
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.RosaClaro
import com.milsabores.appkotlin_guia.viewmodel.MainViewModel
import com.milsabores.appkotlin_guia.viewmodel.PerfilViewModel
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    mainVm: MainViewModel = viewModel(),
    usuarioVm: UsuarioViewModel = viewModel(),
    perfilVm: PerfilViewModel = viewModel(),
    prefs: EstadoDataStore = EstadoDataStore(LocalContext.current),
    cartCount: Int = 0,
    isLoggedInOverride: Boolean? = null
) {
    val scope = rememberCoroutineScope()
    val ui by usuarioVm.estado.collectAsState()
    val isLoggedInFlow by prefs.isLoggedIn.collectAsState(initial = false)
    val isLoggedIn = isLoggedInOverride ?: isLoggedInFlow
    val foto by perfilVm.fotoUri.collectAsState()

    // Launchers (galería / cámara / permiso)
    val pickImage = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        perfilVm.setFromGallery(uri ?: Uri.EMPTY)
    }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(TakePicture()) { success ->
        pendingUri?.let { perfilVm.setFromCamera(success, it) }
    }
    val requestCamera = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            val out = perfilVm.createTempImageUri()
            pendingUri = out
            takePicture.launch(out)
        }
    }

    val snack = remember { SnackbarHostState() }

    // 💡 REMOVIDO: showPwdDialog y CambiarPasswordDialog

    Scaffold(
        containerColor = BlancoDos,
        topBar = {
            TopAppBar(
                title = { Text(if (isLoggedIn) "Mi Perfil" else "Invitado", color = Chocolate) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Chocolate)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoDos)
            )
        },
        snackbarHost = { SnackbarHost(snack) },
        // 💡 REMOVIDA la BottomNavBar (ya se hizo en pasos previos)
        bottomBar = { Spacer(Modifier.height(0.dp)) }
    ) { pv ->
        if (!isLoggedIn) {
            // Vista para invitado
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Estás como invitado", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Chocolate)
                Spacer(Modifier.height(8.dp))
                Text("Inicia sesión para editar tu perfil y comprar más rápido.", color = Chocolate.copy(alpha = 0.8f))
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(AppRoute.Login.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Chocolate,
                        contentColor = RosaClaro
                    )
                ) { Text("Iniciar sesión", fontWeight = FontWeight.SemiBold) }
            }
            return@Scaffold
        }

        // Colores de campos
        val textFieldColors = colors(
            focusedBorderColor = Chocolate,
            unfocusedBorderColor = Chocolate.copy(alpha = 0.5f),
            focusedLabelColor = Chocolate,
            cursorColor = Chocolate,
            unfocusedContainerColor = BlancoDos,
            focusedContainerColor = BlancoDos
        )

        // Usuario autenticado → perfil editable
        LazyColumn( // 💡 CAMBIO: Usamos LazyColumn para asegurar el scroll
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Editar perfil", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = Chocolate)
            }

            // Imagen de perfil y acciones
            item { ImagenInteligente(uri = foto) }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { pickImage.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Chocolate, contentColor = RosaClaro)
                    ) { Text("Galería") }
                    Button(
                        onClick = { requestCamera.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Chocolate, contentColor = RosaClaro)
                    ) { Text("Cámara") }
                }
            }
            item {
                OutlinedButton(
                    onClick = { perfilVm.clear() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Chocolate)
                ) { Text("Quitar imagen") }
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Chocolate.copy(alpha = 0.3f))
            }




            // DATOS EDITABLES
            item {
                OutlinedTextField(
                    value = ui.nombre,
                    onValueChange = usuarioVm::onNombreChange,
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
            }
            // 💡 REMOVIDO: Campo de Correo
            item {
                OutlinedTextField(
                    value = ui.direccion,
                    onValueChange = usuarioVm::onDireccionChange,
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Chocolate.copy(alpha = 0.3f))
            }

            // 💡 NUEVA SECCIÓN: OPCIONES DE USUARIO
            item {
                Text("Opciones", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = Chocolate, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
            }

            // 💡 Historial de pedidos
            item {
                ProfileOptionItem(
                    text = "Historial de Pedidos",
                    icon = Icons.Default.History,
                    // 💡 CAMBIO: onClick vacío
                    onClick = { /* TODO: Implementar navegación a Historial de Pedidos */ },
                    tintColor = Chocolate
                )
            }

            // 💡 Configuraciones
            item {
                ProfileOptionItem(
                    text = "Configuraciones y Tema",
                    icon = Icons.Default.Settings,
                    // 💡 CAMBIO: onClick vacío
                    onClick = { /* TODO: Implementar navegación a Configuraciones */ },
                    tintColor = Chocolate
                )
            }

            // 💡 Preferencias (la dejaremos también sin acción)
            item {
                ProfileOptionItem(
                    text = "Preferencias de Compra",
                    icon = Icons.Default.Star,
                    // 💡 CAMBIO: onClick vacío
                    onClick = { /* TODO: Implementar navegación a Preferencias */ },
                    tintColor = Chocolate
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Chocolate.copy(alpha = 0.3f))
            }


            // 💡 BOTONES DE ACCIÓN (Guardar / Cerrar Sesión)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Guardar
                    Button(
                        onClick = {
                            scope.launch {
                                // Lógica de guardado
                                val ok = usuarioVm.guardarPerfilPorCorreo()
                                if (ok) {
                                    snack.showSnackbar("Perfil actualizado")
                                } else {
                                    snack.showSnackbar("No se pudo actualizar el perfil")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Chocolate,
                            contentColor = RosaClaro
                        )
                    ) { Text("Guardar", fontWeight = FontWeight.SemiBold) }

                    // Botón Cerrar Sesión
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                prefs.setLoggedIn(false)
                                prefs.setGuestMode(false)
                                snack.showSnackbar("Sesión cerrada")
                            }
                            navController.navigate(AppRoute.Entry.route) {
                                popUpTo(AppRoute.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Chocolate),
                        border = BorderStroke(width = 1.dp, color = Chocolate)
                    ) {
                        Text("Cerrar sesión")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", modifier = Modifier.size(18.dp))
                    }
                }
            }


            item { Spacer(Modifier.height(80.dp)) }
        }


    }
}

// 💡 Componente de lista reutilizable para las opciones de perfil
@Composable
private fun ProfileOptionItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tintColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = tintColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = tintColor.copy(alpha = 0.6f)
        )
    }
}

