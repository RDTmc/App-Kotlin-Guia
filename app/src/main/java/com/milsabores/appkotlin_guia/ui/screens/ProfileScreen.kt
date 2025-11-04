package com.milsabores.appkotlin_guia.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.EstadoDataStore
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.ImagenInteligente
import com.milsabores.appkotlin_guia.ui.components.BottomNavBar
import com.milsabores.appkotlin_guia.ui.components.BottomDest
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
    showBottomBar: Boolean = true,        // si quieres ocultarla en algunos flujos
    cartCount: Int = 0,                   // pasa el valor real si lo tienes
    isLoggedInOverride: Boolean? = null   // si ya calculaste isLoggedIn fuera, puedes inyectarlo
) {
    val scope = rememberCoroutineScope()

    // Estado del perfil
    val ui by usuarioVm.estado.collectAsState()

    // Flags de sesión
    val isLoggedInFlow by prefs.isLoggedIn.collectAsState(initial = false)
    val isLoggedIn = isLoggedInOverride ?: isLoggedInFlow

    // Si manejas email guardado en DataStore, puedes precargar usuario aquí:
    // val userEmail by prefs.userEmail.collectAsState(initial = null)
    // LaunchedEffect(userEmail) { userEmail?.let { usuarioVm.cargarUsuarioPorCorreo(it) } }

    // Foto de perfil (tu VM de imagen)
    val foto by perfilVm.fotoUri.collectAsState()

    // Launchers: Galería y Cámara
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

    // Snackbar para feedback
    val snack = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isLoggedIn) "Mi perfil" else "Invitado") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snack) },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    current = BottomDest.PROFILE,
                    cartCount = cartCount,
                    isLoggedIn = isLoggedIn,
                    onSelect = { dest ->
                        when (dest) {
                            BottomDest.HOME -> navController.navigate(AppRoute.Home.route)
                            BottomDest.MENU -> navController.navigate(AppRoute.Resumen.route)
                            BottomDest.CART -> navController.navigate(AppRoute.Cart.route)
                            BottomDest.PROFILE -> Unit
                        }
                    }
                )
            }
        }
    ) { pv ->
        if (!isLoggedIn) {
            // Vista invitado → invitar a iniciar sesión
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Estás como invitado", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Inicia sesión para editar tu perfil y comprar más rápido.")
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(AppRoute.Login.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF573123),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) { Text("Iniciar sesión") }
            }
            return@Scaffold
        }

        // Usuario logeado → perfil editable
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Perfil de usuario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Imagen circular reutilizable
            ImagenInteligente(uri = foto)

            // Acciones: Galería y Cámara
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { pickImage.launch("image/*") }) { Text("Desde galería") }
                Button(onClick = { requestCamera.launch(Manifest.permission.CAMERA) }) { Text("Usar cámara") }
            }

            OutlinedButton(onClick = { perfilVm.clear() }) { Text("Quitar imagen") }

            // Datos de cuenta
            OutlinedTextField(
                value = ui.nombre,
                onValueChange = usuarioVm::onNombreChange,
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.correo,
                onValueChange = {}, // Email solo lectura en perfil
                label = { Text("Email") },
                singleLine = true,
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.direccion,
                onValueChange = usuarioVm::onDireccionChange,
                label = { Text("Dirección") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Guardar cambios de perfil:
                        // Requiere un método en VM que actualice por correo (ver snippet abajo).
                        scope.launch {
                            val ok = usuarioVm.guardarPerfilPorCorreo() // <-- ver implementación sugerida
                            if (ok) {
                                snack.showSnackbar("Perfil actualizado")
                            } else {
                                snack.showSnackbar("No se pudo actualizar el perfil")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF573123),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) { Text("Guardar") }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            prefs.setLoggedIn(false)
                            prefs.setGuestMode(false)
                            // Si guardas email en DataStore y quieres “cerrar sesión total”, añade prefs.clearUserEmail()
                            snack.showSnackbar("Sesión cerrada")
                        }
                        navController.navigate(AppRoute.Entry.route) {
                            popUpTo(AppRoute.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Cerrar sesión") }
            }
        }
    }
}
