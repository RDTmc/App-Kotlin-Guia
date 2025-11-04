package com.milsabores.appkotlin_guia.ui.util

import com.milsabores.appkotlin_guia.model.Product

/**
 * Detecta si el producto es tipo "torta/cheesecake" por nombre/categoría/tags.
 */
fun isCakeLike(p: Product): Boolean {
    val n = p.nombre.lowercase()
    val c = p.categoria.lowercase()
    val t = p.tags.joinToString(",").lowercase()
    return n.contains("torta") ||
            n.contains("cheesecake") ||
            c.contains("torta") ||
            t.contains("cumple") ||        // "cumpleaños"
            t.contains("boda") ||            // bodas
            t.contains("especial") ||
            t.contains("cheesecake")
}

/** Devuelve la lista de tamaños que debe mostrar la UI según el tipo de producto. */
fun sizesFor(p: Product): List<String> {
    return if (isCakeLike(p)) {
        listOf("8 porciones", "10 porciones", "12 porciones")
    } else {
        listOf("Chico", "Mediano", "Grande")
    }
}

/**
 * Calcula precio final a partir del precio base y el tamaño seleccionado.
 * Reglas:
 *  - Tortas/Cheesecake (porciones):
 *      8  → base
 *      10 → base + 3.000
 *      12 → base + 5.000
 *  - Otros (tallas):
 *      Chico   → base
 *      Mediano → base + 1.500
 *      Grande  → base + 3.000
 */
fun priceFor(p: Product, base: Int, size: String?): Int {
    if (size.isNullOrBlank()) return base
    return if (isCakeLike(p)) {
        when (size) {
            "10 porciones" -> base + 3000
            "12 porciones" -> base + 5000
            else -> base // "8 porciones" u otro default
        }
    } else {
        when (size) {
            "Mediano" -> base + 1500
            "Grande"  -> base + 3000
            else -> base // "Chico" u otro default
        }
    }
}
