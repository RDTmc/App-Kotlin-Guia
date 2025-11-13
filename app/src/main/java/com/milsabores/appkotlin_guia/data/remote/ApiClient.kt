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

// ⚠️ Reemplaza por la IP local de tu PC
private const val BASE_URL = "http://10.199.140.94:9090/api/"

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 12,
        @Query("q") q: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sort") sort: String? = null // ej: "name" o "-price"
    ): PagedProductsDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto
}

object ApiClient {
    val service: ApiService by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val ok = OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(ok)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}