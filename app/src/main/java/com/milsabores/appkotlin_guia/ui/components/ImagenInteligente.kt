package com.milsabores.appkotlin_guia.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImagenInteligente(uri: Uri?, modifier: Modifier = Modifier) {
    if (uri != null) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Foto de perfil",
            modifier = modifier
                .size(160.dp)
                .clip(CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Foto de perfil",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .size(160.dp)
                .clip(CircleShape)
        )
    }
}