package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.milsabores.appkotlin_guia.model.HomeFilter

@Composable
fun FilterCategoriaRow(
    selected: HomeFilter,
    onSelected: (HomeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val chips = listOf(
            "Cumpleaños" to HomeFilter.CUMPLEANOS,
            "Bodas" to HomeFilter.BODAS,
            "Sin azúcar" to HomeFilter.SIN_AZUCAR,
            "Vegano" to HomeFilter.VEGANO
        )
        chips.forEach { (label, key) ->
            FilterChip(
                selected = selected == key,
                onClick = { onSelected(key) },
                label = { Text(label) }
            )
        }
    }
}


