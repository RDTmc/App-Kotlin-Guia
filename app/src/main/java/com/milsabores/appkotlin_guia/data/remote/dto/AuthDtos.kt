package com.milsabores.appkotlin_guia.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//  REQUESTS

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "fullName") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "phone") val phone: String?,
    @Json(name = "address") val address: String?
)

// RESPONSE REAL DEL BACKEND

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @Json(name = "token") val token: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "email") val email: String,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "role") val role: String
)

// MODELOS DE DOMINIO (NO LOS PARSEA MOSHI DIRECTO)

data class RemoteUserDto(
    val id: String,
    val email: String?,
    val fullName: String?,
    val role: String?,
    val address: String?
)

data class AuthSession(
    val token: String,
    val user: RemoteUserDto
)
