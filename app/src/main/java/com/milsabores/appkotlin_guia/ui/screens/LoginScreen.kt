package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
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
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.RosaClaro
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
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        containerColor = BlancoDos,
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
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Chocolate,
                    unfocusedBorderColor = Chocolate.copy(alpha = 0.5f),
                    focusedLabelColor = Chocolate,
                    cursorColor = Chocolate,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description, tint = Chocolate)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Chocolate,
                    unfocusedBorderColor = Chocolate.copy(alpha = 0.5f),
                    focusedLabelColor = Chocolate,
                    cursorColor = Chocolate,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
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
                    usuarioVm.login(email, pass) { ok, user ->
                        if (ok && user != null) {
                            scope.launch {
                                prefs.setGuestMode(false)
                                prefs.setLoggedIn(true)
                                prefs.setUserEmail(user.correo)
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
                    containerColor = Chocolate,
                    contentColor = RosaClaro
                )
            ) {
                Text("Ingresar", fontWeight = FontWeight.SemiBold)
            }

            TextButton(
                onClick = { navController.navigate(AppRoute.Register.route) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Chocolate // Texto del botón en Chocolate
                )
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}