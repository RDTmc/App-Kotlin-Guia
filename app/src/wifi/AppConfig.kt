package com.milsabores.appkotlin_guia

object AppConfig {
    // IP cuando estás conectado por WiFi
    // Para encontrar tu IP en Windows: ipconfig
    // Para encontrar tu IP en Mac/Linux: ifconfig
    const val API_BASE_URL = "http://192.168.1.100:9090/api/"

    const val DEBUG = true
    const val NETWORK_TYPE = "WiFi"
}