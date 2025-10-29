package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.ViewModel
import com.milsabores.appkotlin_guia.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CatalogViewModel : ViewModel() {

    // Seed de productos destacados
    private val destacados: List<Product> = listOf(
        Product("TC001", "Tortas Cuadradas", "Torta Cuadrada de Chocolate", 45000, "img/tc_chocolate.jpg",
            "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
            tags = listOf("tradicional")),
        Product("TT001", "Tortas Circulares", "Torta Circular de Vainilla", 40000, "img/tt_vainilla.png",
            "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.",
            tags = listOf("tradicional")),
        Product("PI001", "Postres Individuales", "Mousse de Chocolate", 5000, "img/pi_mousse.png",
            "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
            tags = listOf("individual")),
        Product("PSA001", "Sin Azúcar", "Torta Sin Azúcar de Naranja", 48000, "img/psa_naranja.png",
            "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables.",
            tags = listOf("sin azúcar"))
    )

    // Seed para catálogo (fuente de verdad)
    private val catalogo: List<Product> = listOf(
        Product("TC001","Tortas Cuadradas","Torta Cuadrada de Chocolate",45000,"img/tc_chocolate.png",
            "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
            tags = listOf("tradicional"), tamanos = listOf("8 porciones","10 porciones","12 porciones")),
        Product("TC002","Tortas Cuadradas","Torta Cuadrada de Frutas",50000,"img/tc_frutas.png",
            "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones.",
            tags = listOf("tradicional")),
        Product("TT001","Tortas Circulares","Torta Circular de Vainilla",40000,"img/tt_vainilla.png",
            "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.",
            tags = listOf("tradicional")),
        Product("TT002","Tortas Circulares","Torta Circular de Manjar",42000,"img/tt_manjar.png",
            "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos.",
            tags = listOf("tradicional")),
        Product("PI001","Postres Individuales","Mousse de Chocolate",5000,"img/pi_mousse.png",
            "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
            tags = listOf("individual")),
        Product("PI002","Postres Individuales","Tiramisú Clásico",5500,"img/pi_tiramisu.png",
            "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida.",
            tags = listOf("individual")),
        Product("PSA001","Sin Azúcar","Torta Sin Azúcar de Naranja",48000,"img/psa_naranja.png",
            "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables.",
            tags = listOf("sin azúcar")),
        Product("PSA002","Sin Azúcar","Cheesecake Sin Azúcar",47000,"img/psa_cheesecake.png",
            "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa.",
            tags = listOf("sin azúcar")),
        Product("PG001","Sin Gluten","Brownie Sin Gluten",4000,"img/pg_brownie.png",
            "Rico y denso, este brownie es perfecto para quienes necesitan evitar el gluten sin sacrificar el sabor.",
            tags = listOf("sin gluten")),
        Product("PG002","Sin Gluten","Pan Sin Gluten",3500,"img/pg_pan.png",
            "Suave y esponjoso, ideal para sándwiches o para acompañar cualquier comida.",
            tags = listOf("sin gluten")),
        Product("PV001","Vegano","Torta Vegana de Chocolate",50000,"img/pv_chocolate.png",
            "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal, perfecta para veganos.",
            tags = listOf("vegano")),
        Product("PV002","Vegano","Galletas Veganas de Avena",4500,"img/pv_avena.png",
            "Crujientes y sabrosas, estas galletas son una excelente opción para un snack saludable y vegano.",
            tags = listOf("vegano")),
        Product("TE001","Tortas Especiales","Torta Especial de Cumpleaños",55000,"img/te_cumple.png",
            "Diseñada especialmente para celebraciones, personalizable con decoraciones y mensajes únicos.",
            tags = listOf("especial", "cumpleaños")),
        Product("TE002","Tortas Especiales","Torta Especial de Boda",60000,"img/te_boda.png",
            "Elegante y deliciosa, esta torta está diseñada para ser el centro de atención en cualquier boda.",
            tags = listOf("especial", "boda"))
    )

    // Estado expuesto
    private val _filter = MutableStateFlow<String?>(null)         // null = "Todos"
    val filter: StateFlow<String?> = _filter

    private val _featured = MutableStateFlow(destacados.take(3))
    val featured: StateFlow<List<Product>> = _featured

    private val _products = MutableStateFlow(catalogo)
    val products: StateFlow<List<Product>> = _products

    /** f = null -> "Todos" */
    fun setFilter(f: String?) {
        _filter.value = f
        _products.value = when (f) {
            null, "Todos" -> catalogo
            "Cumpleaños"  -> catalogo.filter {
                it.categoria == "Tortas Especiales" ||
                        it.nombre.contains("Cumple", ignoreCase = true) ||
                        it.tags.any { t -> t.equals("cumpleaños", ignoreCase = true) }
            }
            "Bodas"       -> catalogo.filter {
                it.categoria == "Tortas Especiales" ||
                        it.nombre.contains("Boda", ignoreCase = true) ||
                        it.tags.any { t -> t.equals("boda", ignoreCase = true) }
            }
            "Sin azúcar"  -> catalogo.filter {
                it.categoria.equals("Sin Azúcar", ignoreCase = true) ||
                        it.tags.any { t -> t.equals("sin azúcar", ignoreCase = true) || t.equals("sinazucar", true) }
            }
            "Vegano"      -> catalogo.filter {
                it.categoria.equals("Vegano", ignoreCase = true) ||
                        it.tags.any { t -> t.equals("vegano", ignoreCase = true) }
            }
            else -> catalogo
        }
    }

    /** Producto por id (incluye destacados) */
    fun getProduct(id: String): Product? =
        (catalogo + destacados).firstOrNull { it.id == id }

    /** Similares por categoría o tags, desde el catálogo completo */
    fun getSimilar(to: Product, limit: Int = 10): List<Product> =
        catalogo.filter { it.id != to.id && (it.categoria == to.categoria || it.tags.any { t -> to.tags.contains(t) }) }
            .take(limit)
}
