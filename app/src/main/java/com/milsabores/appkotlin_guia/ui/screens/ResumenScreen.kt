package com.milsabores.appkotlin_guia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.ui.theme.BlancoDos
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    onEditProfile: () -> Unit = {},
    onGoCart: () -> Unit = {},
    onChangePassword: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()

    val mostrarPass = remember { mutableStateOf(false) }
    val filled = listOf(
        estado.nombre.isNotBlank(),
        estado.correo.isNotBlank(),
        estado.contrasena.isNotBlank(),
        estado.direccion.isNotBlank()
    ).count { it }
    val progress = filled / 4f

    Scaffold(
        containerColor = BlancoDos,
        topBar = {
            TopAppBar(title = { Text("Resumen de Registro") })
        }
    ) { pv ->
        Column(Modifier.padding(pv).padding(16.dp)) {

            // Progreso
            Text(
                "Completitud del perfil: ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))

            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Nombre"); Text(estado.nombre.ifBlank { "—" })
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Correo"); Text(estado.correo.ifBlank { "—" })
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Dirección"); Text(estado.direccion.ifBlank { "—" })
                    }

                    Divider(Modifier.padding(vertical = 8.dp))

                    val passRender = if (mostrarPass.value) estado.contrasena else "*".repeat(estado.contrasena.length)
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Contraseña")
                        Row {
                            Text(passRender)
                            TextButton(onClick = { mostrarPass.value = !mostrarPass.value }) {
                                Text(if (mostrarPass.value) "Ocultar" else "Ver")
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Términos")
                        Text(if (estado.aceptaTerminos) "Sí" else "No")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Siguiente paso sugerido", fontWeight = FontWeight.SemiBold)
                    Text(
                        when {
                            !estado.aceptaTerminos -> "Debes aceptar los términos y condiciones."
                            estado.nombre.isBlank() || estado.direccion.isBlank() ->
                                "Completa tu nombre y dirección para acelerar la entrega."
                            else -> "¡Todo listo! Puedes continuar con la compra o editar tu perfil."
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditProfile, modifier = Modifier.weight(1f)) {
                    Text("Editar perfil")
                }
                OutlinedButton(onClick = onGoCart, modifier = Modifier.weight(1f)) {
                    Text("Ir al carrito")
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar contraseña")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.onNombreChange("")
                    viewModel.onCorreoChange("")       // Si prefieres, no limpies el correo
                    viewModel.onContrasenaChange("")
                    viewModel.onDireccionChange("")
                    viewModel.onAceptarTerminosChange(false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar datos del resumen")
            }
        }
    }
}
