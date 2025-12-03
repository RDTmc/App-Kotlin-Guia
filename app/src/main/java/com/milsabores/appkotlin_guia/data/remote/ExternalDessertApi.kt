package com.milsabores.appkotlin_guia.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class MealDto(
    @Json(name = "idMeal") val idMeal: String,
    @Json(name = "strMeal") val name: String,
    @Json(name = "strMealThumb") val thumbnail: String
)

@JsonClass(generateAdapter = true)
data class MealsResponseDto(
    @Json(name = "meals") val meals: List<MealDto>?
)

interface DessertApiService {

    // Ejemplo: filter.php?c=Dessert
    @GET("filter.php")
    suspend fun getDesserts(
        @Query("c") category: String = "Dessert"
    ): MealsResponseDto
}

object DessertApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val service: DessertApiService by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DessertApiService::class.java)
    }
}
