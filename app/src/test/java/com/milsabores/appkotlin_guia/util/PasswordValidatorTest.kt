package com.milsabores.appkotlin_guia.util

import com.milsabores.appkotlin_guia.ui.util.PasswordValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun `password valida cumple todas las reglas`() {
        val pwd = "Kotlin123"
        val result = PasswordValidator.isValid(pwd)
        assertTrue(result)
    }

    @Test
    fun `password corta es invalida`() {
        val pwd = "Ko12"
        val result = PasswordValidator.isValid(pwd)
        assertFalse(result)
    }

    @Test
    fun `password sin mayuscula es invalida`() {
        val pwd = "kotlin123"
        val result = PasswordValidator.isValid(pwd)
        assertFalse(result)
    }

    @Test
    fun `password sin numero es invalida`() {
        val pwd = "KotlinAA"
        val result = PasswordValidator.isValid(pwd)
        assertFalse(result)
    }
}
