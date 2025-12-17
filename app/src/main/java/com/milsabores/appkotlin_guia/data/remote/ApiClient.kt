package com.milsabores.appkotlin_guia.data.remote

import com.milsabores.appkotlin_guia.data.remote.dto.PagedProductsDto
import com.milsabores.appkotlin_guia.data.remote.dto.ProductDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Configuración centralizada
private object NetworkConfig {
    // Cambia a la IP actual de tu PC en la misma red del teléfono
    // Ej.: 192.168.1.82 según tu último mensaje
    // const val BASE_URL = "http://192.168.1.82:9090/api/"
    const val BASE_URL = "http://192.168.1.83:8081/api/"

    // Ejemplos alternativos (descomenta si los usas):
    // const val BASE_URL = "http://10.65.206.94:9090/api/"   // Datos móviles
    // const val BASE_URL = "https://api.milsabores.com/api/" // Producción

    const val DEBUG = true
}

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 12,
        @Query("q") q: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sort") sort: String? = null // p.ej. "name" o "-price"
    ): PagedProductsDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto
}

object ApiClient {
    val service: ApiService by lazy {
        // Logger HTTP (solo en debug)
        val logger = HttpLoggingInterceptor().apply {
            level = if (NetworkConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // OkHttp
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        // Moshi con soporte para Kotlin (reflexión)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // Retrofit
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}
