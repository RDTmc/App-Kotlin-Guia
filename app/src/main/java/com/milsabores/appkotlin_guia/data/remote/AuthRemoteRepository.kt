package com.milsabores.appkotlin_guia.data.remote

import com.milsabores.appkotlin_guia.data.remote.dto.AuthUserDto
import com.milsabores.appkotlin_guia.data.remote.dto.LoginRequestDto
import com.milsabores.appkotlin_guia.data.remote.dto.RegisterRequestDto

data class AuthSession(
    val token: String,
    val user: AuthUserDto
)

class AuthRemoteRepository(
    private val api: AuthApiService = AuthApiClient.service
) {
    suspend fun login(email: String, password: String): AuthSession {
        val resp = api.login(LoginRequestDto(email = email, password = password))
        val token = resp.token ?: throw IllegalStateException("Respuesta de login sin token")
        val user = resp.user ?: throw IllegalStateException("Respuesta de login sin usuario")
        return AuthSession(token, user)
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String? = null,
        address: String? = null
    ): AuthSession {
        val resp = api.register(
            RegisterRequestDto(
                fullName = fullName,
                email = email,
                password = password,
                phone = phone,
                address = address
            )
        )
        val token = resp.token ?: "" // el registro podría no devolver token y no queremos romper
        val user = resp.user
            ?: AuthUserDto(
                id = null,
                email = email,
                fullName = fullName,
                phone = phone,
                role = null,
                birthDate = null,
                registrationCode = null,
                address = address
            )
        return AuthSession(token, user)
    }
}
