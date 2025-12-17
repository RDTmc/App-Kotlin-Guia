package com.milsabores.appkotlin_guia.data.remote

import com.milsabores.appkotlin_guia.data.remote.dto.AuthSession
import com.milsabores.appkotlin_guia.data.remote.dto.LoginRequestDto
import com.milsabores.appkotlin_guia.data.remote.dto.LoginResponseDto
import com.milsabores.appkotlin_guia.data.remote.dto.RegisterRequestDto
import com.milsabores.appkotlin_guia.data.remote.dto.RemoteUserDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object AuthNetworkConfig {
    // Usa la IP/puerto reales de ms-usuarios
    const val BASE_URL = "http://192.168.1.83:8082/api/"
    const val DEBUG = true
}

interface AuthApi {

    // AHORA devolvemos LoginResponseDto, que coincide con el JSON real
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto)
}

object AuthApiClient {
    val service: AuthApi by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = if (AuthNetworkConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl(AuthNetworkConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AuthApi::class.java)
    }
}

class AuthRemoteRepository(
    private val api: AuthApi = AuthApiClient.service
) {
    suspend fun login(email: String, password: String): AuthSession {
        val dto = api.login(LoginRequestDto(email = email, password = password))

        // Mapear DTO de red -> modelo de dominio
        val user = RemoteUserDto(
            id = dto.userId,
            email = dto.email,
            fullName = dto.fullName,
            role = dto.role,
            address = null // no lo vamos a usar
        )

        return AuthSession(
            token = dto.token,
            user = user
        )
    }

    // Registro remoto

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String?,
        address: String?
    ) {
        api.register(
            RegisterRequestDto(
                fullName = fullName,
                email = email,
                password = password,
                phone = phone,
                address = address
            )
        )
    }
}
