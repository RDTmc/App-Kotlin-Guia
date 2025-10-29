package com.milsabores.appkotlin_guia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterChipsRow(
    selected: String?,                  // null = Todos
    onSelected: (String?) -> Unit
) {
    val opts = listOf("Cumpleaños", "Bodas", "Sin azúcar", "Vegano", "Todos")

    val baseStyle = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),                // antes 12.dp
        horizontalArrangement = Arrangement.spacedBy(2.dp) // antes 4–6.dp
    ) {
        opts.forEach { label ->
            // 👇 el de "Sin azúcar" recibe un poquito más de ancho
            val weight = if (label == "Sin azúcar") 1.2f else 1f
            Box(modifier = Modifier.weight(weight)) {
                val isSelected = when (label) {
                    "Todos" -> selected == null || selected == "Todos"
                    else    -> selected == label
                }

                // Texto corto + tamaño ajustado solo para "Sin azúcar"
                val short = when (label) {
                    "Cumpleaños" -> "Cumple"
                    "Sin azúcar" -> "Sin Azúcar"      // mantenemos tu rótulo
                    else -> label
                }
                val style = if (label == "Sin azúcar")
                    baseStyle.copy(fontSize = 11.sp) // 1pt menos solo aquí
                else
                    baseStyle

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(if (label == "Todos") null else label) },
                    label = {
                        Text(
                            text = short,
                            maxLines = 1,
                            overflow = TextOverflow.Clip, // sin "..."
                            style = style
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
