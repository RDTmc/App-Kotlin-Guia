package com.milsabores.appkotlin_guia.ui.screens.responsive

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun ActivityCompact(paddingValues: PaddingValues = PaddingValues()) {
    var nombre by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_pasteleria),
            contentDescription = "Imagen compact",
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Fit
        )

        Text("Compact: layout en columna")

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { /* validar / viewModel */ }) {
            Text("Guardar")
        }
    }
}
