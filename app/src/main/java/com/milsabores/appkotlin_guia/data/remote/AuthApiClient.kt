package com.milsabores.appkotlin_guia.data.remote

import com.milsabores.appkotlin_guia.data.remote.dto.LoginRequestDto
import com.milsabores.appkotlin_guia.data.remote.dto.LoginResponseDto
import com.milsabores.appkotlin_guia.data.remote.dto.RegisterRequestDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private object AuthNetworkConfig {
    // AJUSTA ESTA URL AL PUERTO/IP REAL DE ms-usuarios
    // Ej: http://192.168.1.2:8082/api/
    const val BASE_URL = "http://192.168.1.82:8082/api/"
    const val DEBUG = true
}

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): LoginResponseDto
}

object AuthApiClient {
    val service: AuthApiService by lazy {
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
            .create(AuthApiService::class.java)
    }
}
