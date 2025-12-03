package com.milsabores.appkotlin_guia.ui.util

object PasswordValidator {

    /**
     * Regla de ejemplo para la defensa:
     * - Mínimo 8 caracteres
     * - Al menos una mayúscula
     * - Al menos un número
     */
    fun isValid(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }
}


