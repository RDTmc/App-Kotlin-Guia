package com.milsabores.appkotlin_guia.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthUserDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "birthDate") val birthDate: String? = null,
    @Json(name = "registrationCode") val registrationCode: String? = null,
    @Json(name = "address") val address: String? = null
)

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
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "address") val address: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @Json(name = "token") val token: String?,
    @Json(name = "user") val user: AuthUserDto?
)
