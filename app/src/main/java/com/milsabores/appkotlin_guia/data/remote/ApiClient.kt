package com.milsabores.appkotlin_guia.data.remote

import com.milsabores.appkotlin_guia.data.remote.dto.PagedProductsDto
import com.milsabores.appkotlin_guia.data.remote.dto.ProductDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// 🔧 Configuración centralizada
private object NetworkConfig {
    // ═══════════════════════════════════════════════════════════
    // 📱 ACTIVA LA URL QUE NECESITES (descomenta una línea):
    // ═══════════════════════════════════════════════════════════

    // 🏠 WiFi - Usa tu IP local cuando estés en la misma red WiFi
    const val BASE_URL = "http://192.168.1.100:9090/api/"

    // 📱 Mobile Data - Usa cuando te conectes por datos móviles
    // const val BASE_URL = "http://10.65.206.94:9090/api/"

    // 🌐 Producción - Servidor en internet
    // const val BASE_URL = "https://api.milsabores.com/api/"

    // ═══════════════════════════════════════════════════════════
    const val DEBUG = true
}

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 12,
        @Query("q") q: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sort") sort: String? = null
    ): PagedProductsDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto
}

object ApiClient {
    val service: ApiService by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = if (NetworkConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}