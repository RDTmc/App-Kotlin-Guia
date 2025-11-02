package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.EstadoDataStore
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    usuarioVm: UsuarioViewModel,
    prefs: EstadoDataStore
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inicia sesión") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (email.isBlank() || pass.isBlank()) {
                        error = "Completa tus credenciales"
                        return@Button
                    }
                    // login real usando tu VM
                    usuarioVm.login(email, pass) { ok ->
                        if (ok) {
                            scope.launch {
                                prefs.setGuestMode(false)
                                prefs.setLoggedIn(true)
                            }

                            navController.navigate(AppRoute.Home.route) {
                                popUpTo(AppRoute.Entry.route) { inclusive = true }
                            }
                        } else {
                            error = "Credenciales inválidas"
                            scope.launch {
                                snackbar.showSnackbar("Usuario o contraseña incorrectos")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF573123),
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text("Ingresar")
            }

            TextButton(
                onClick = { navController.navigate(AppRoute.Register.route) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}