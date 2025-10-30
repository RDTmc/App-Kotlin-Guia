package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AuthRequiredDialog(
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onGuest: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Necesitas una cuenta") },
        text = { Text("Para finalizar la compra, inicia sesión o crea una cuenta.") },
        confirmButton = {
            TextButton(onClick = {
                onLogin()
                onDismiss()
            }) { Text("Login") }
        },
        dismissButton = {
            // registro + opcional invitado
            TextButton(onClick = {
                onRegister()
                onDismiss()
            }) { Text("Registro") }
            if (onGuest != null) {
                TextButton(onClick = {
                    onGuest()
                    onDismiss()
                }) { Text("Invitado") }
            }
        }
    )
}