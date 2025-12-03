package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.milsabores.appkotlin_guia.model.Users
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.ui.theme.Chocolate
import com.milsabores.appkotlin_guia.ui.theme.RosaClaro
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(
    viewModel: UsuarioViewModel,
    navController: NavController
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BlancoDos
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                isError = estado.errores.nombre != null,
                singleLine = true,
                supportingText = {
                    estado.errores.nombre?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
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
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Email") },
                isError = estado.errores.correo != null,
                singleLine = true,
                supportingText = {
                    estado.errores.correo?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
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
                value = estado.contrasena,
                onValueChange = viewModel::onContrasenaChange,
                label = { Text("Contraseña") },
                isError = estado.errores.contrasena != null,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = desc, tint = Chocolate)
                    }
                },
                supportingText = {
                    estado.errores.contrasena?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
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
                value = estado.direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Dirección") },
                isError = estado.errores.direccion != null,
                singleLine = true,
                supportingText = {
                    estado.errores.direccion?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = viewModel::onAceptarTerminosChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Chocolate,
                        uncheckedColor = Chocolate.copy(alpha = 0.7f),
                        checkmarkColor = RosaClaro
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text("Acepto los términos y condiciones", color = Chocolate)
            }

            Button(
                onClick = {
                    if (viewModel.estaValidadoElFormulario() && estado.aceptaTerminos) {
                        viewModel.registrarRemoto { ok, msg ->
                            scope.launch {
                                if (ok) {
                                    snackbarHostState.showSnackbar("Usuario registrado")
                                    kotlinx.coroutines.delay(500)
                                    // Después de registrar, vamos al LOGIN
                                    navController.navigate(AppRoute.Login.route) {
                                        popUpTo(AppRoute.Register.route) { inclusive = true }
                                    }
                                } else {
                                    snackbarHostState.showSnackbar(msg ?: "No se pudo registrar el usuario")
                                }
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
                Text("Registrar", fontWeight = FontWeight.SemiBold)
            }


        }
    }
}

@Composable
fun UsuariosListScreen(vm: UsuarioViewModel) {
    val usuarios: List<Users> by vm.usuariosFlow.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = usuarios, key = { it.id }) { u: Users ->
            Column {
                Text(text = "ID: ${u.id}")
                Text(text = "Nombre: ${u.nombre}")
                Text(text = "Correo: ${u.correo}")
            }
        }
    }
}
