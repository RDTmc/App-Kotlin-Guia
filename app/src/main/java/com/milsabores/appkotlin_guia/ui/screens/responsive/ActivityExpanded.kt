package com.milsabores.appkotlin_guia.ui.screens.responsive

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.R

@Composable
fun ActivityExpanded(paddingValues: PaddingValues = PaddingValues()) {
    var nombre by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Columna izquierda (imagen grande)
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Imagen expanded",
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            contentScale = ContentScale.Fit
        )

        // Columna derecha (form y acciones)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Expanded: imagen grande y panel de edición")

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Tu nombre") }
            )

            Button(onClick = { /* validar / viewModel */ }) {
                Text("Guardar")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
