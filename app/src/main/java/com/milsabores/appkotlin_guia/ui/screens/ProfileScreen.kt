package com.milsabores.appkotlin_guia.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.components.ImagenInteligente
import com.milsabores.appkotlin_guia.viewmodel.MainViewModel
import com.milsabores.appkotlin_guia.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavController
) {
    // Boton bar config
    val items = listOf(AppRoute.Home, AppRoute.Profile)
    var selectedItem by remember { mutableIntStateOf(1) }

    // ViewModel de perfil
    val perfilVm: PerfilViewModel = viewModel()
    val foto by perfilVm.fotoUri.collectAsState()

    // Lanzador: Galeria y Camara
    val pickImage = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        perfilVm.setFromGallery(uri?: Uri.EMPTY)
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, approute ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            viewModel.navigateTo(approute)
                        },
                        label = { Text(approute.route) },
                        icon = {
                            Icon(
                                imageVector = if (approute == AppRoute.Home) Icons.Default.Home else Icons.Default.Person,
                                contentDescription = approute.route
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido al Perfil", style = MaterialTheme.typography.titleLarge)

            // Imagen circular reutilizable
            ImagenInteligente(uri = foto)

            // Acciones: Galería y Cámara
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { pickImage.launch("image/*") }) {
                    Text("Desde galería")
                }
                Button(onClick = { requestCamera.launch(Manifest.permission.CAMERA) }) {
                    Text("Usar cámara")
                }
            }

            // Limpia imagen (opcional)
            OutlinedButton(onClick = { perfilVm.clear() }) {
                Text("Quitar imagen")
            }
        }
    }
}
