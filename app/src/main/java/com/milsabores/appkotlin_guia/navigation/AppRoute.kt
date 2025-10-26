package com.milsabores.appkotlin_guia.navigation

sealed class AppRoute(val route:String) {
    data object Home:AppRoute("home")
    data object Register: AppRoute("register")
    data object Profile: AppRoute("profile")
    data object Settings: AppRoute("settings")

    data object Resumen : AppRoute("resumen")

    data class Detail (val itemId:String): AppRoute("detail/{itemId}")
    {
        fun buildRoute():String{
            return route.replace("{itemId}",itemId)
        }
    }
}