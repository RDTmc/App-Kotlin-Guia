package com.milsabores.appkotlin_guia.navigation

sealed class AppRoute(val route:String) {

    data object Splash     : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object Entry      : AppRoute("entry")
    data object Home:AppRoute("home")
    data object Register: AppRoute("register")
    data object Profile: AppRoute("profile")
    data object Settings: AppRoute("settings")

    data object Resumen : AppRoute("resumen")

    data object Estado : AppRoute("estado")

    data object Cart : AppRoute("cart")
    data object Checkout   : AppRoute("checkout")


    data class Detail(val itemId: String) : AppRoute("detail/{itemId}") {
        fun buildRoute(): String = route.replace("{itemId}", itemId)
    }

    data object Product : AppRoute("product/{id}") {
        fun build(id: String) = "product/$id"
        const val ARG_ID = "id"
    }
}