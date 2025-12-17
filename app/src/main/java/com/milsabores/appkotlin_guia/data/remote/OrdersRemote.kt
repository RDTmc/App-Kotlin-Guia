package com.milsabores.appkotlin_guia.data.remote

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// === Configuración de red para ms-orders ===
private object OrdersNetworkConfig {
    // IMPORTANTE: ajusta IP si cambia tu red local
    // ms-orders: port 8083, context-path /api  ->  http://IP:8083/api/
    const val BASE_URL = "http://192.168.1.82:8083/api/"
    const val DEBUG = true
}

/* ===========================
 * DTOs para ms-orders
 * Coinciden con OrderDtos de tu backend
 * =========================== */

@JsonClass(generateAdapter = true)
data class CreateOrderItemDto(
    val productId: String,
    val productName: String,
    val image: String? = null,
    val unitPrice: Int,
    val quantity: Int,
    val size: String? = null,
    val flavor: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateOrderRequestDto(
    val paymentMethod: String,
    val shippingAddress: String,
    val items: List<CreateOrderItemDto>,
    val discountCode: String? = null
)

@JsonClass(generateAdapter = true)
data class OrderItemResponseDto(
    val id: String?,
    val productId: String?,
    val productName: String?,
    val image: String?,
    val unitPrice: Int?,
    val quantity: Int?,
    val size: String?,
    val flavor: String?
)

@JsonClass(generateAdapter = true)
data class OrderResponseDto(
    val id: String?,
    val userId: String?,
    val status: String?,
    val subtotalAmount: Int?,
    val discountAmount: Int?,
    val discountCode: String?,
    val discountDescription: String?,
    val totalAmount: Int?,
    val paymentMethod: String?,
    val shippingAddress: String?,
    // En backend es LocalDateTime, aquí lo manejamos como String
    val createdAt: String?,
    val items: List<OrderItemResponseDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class OrderErrorBodyDto(
    val message: String?
)

/* ===========================
 * Interfaz Retrofit para ms-orders
 * =========================== */

interface OrdersApi {

    /**
     * POST /api/orders
     * Crea una orden.
     *
     * Requiere:
     *  - Authorization: "Bearer <token>"
     *  - Body: CreateOrderRequestDto
     *  - X-User-Id opcional (back lo usa como respaldo)
     */
    @POST("orders")
    suspend fun createOrder(
        @Body body: CreateOrderRequestDto,
        @Header("Authorization") authHeader: String,
        @Header("X-User-Id") userIdHeader: String? = null
    ): OrderResponseDto

    /**
     * GET /api/orders
     * Lista las órdenes del usuario.
     */
    @GET("orders")
    suspend fun listOrders(
        @Header("Authorization") authHeader: String,
        @Header("X-User-Id") userIdHeader: String? = null
    ): List<OrderResponseDto>

    /**
     * GET /api/orders/{orderId}
     * Detalle de una orden.
     */
    @GET("orders/{orderId}")
    suspend fun getOrder(
        @Path("orderId") orderId: String,
        @Header("Authorization") authHeader: String,
        @Header("X-User-Id") userIdHeader: String? = null
    ): OrderResponseDto
}

/* ===========================
 * Cliente Retrofit
 * =========================== */

object OrdersApiClient {

    val service: OrdersApi by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = if (OrdersNetworkConfig.DEBUG) {
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
            .baseUrl(OrdersNetworkConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OrdersApi::class.java)
    }
}

/* ===========================
 * Repositorio remoto de órdenes
 * (capa fina para usar desde ViewModel)
 * =========================== */

class OrdersRemoteRepository(
    private val api: OrdersApi = OrdersApiClient.service
) {

    /**
     * Crea una orden en ms-orders.
     *
     * @param token JWT emitido por ms-usuarios (sin "Bearer ")
     * @param request payload con pago, dirección e items
     * @param userId opcional para X-User-Id (respaldo)
     */
    suspend fun createOrder(
        token: String,
        request: CreateOrderRequestDto,
        userId: String? = null
    ): OrderResponseDto {
        val auth = "Bearer $token"
        return api.createOrder(
            body = request,
            authHeader = auth,
            userIdHeader = userId
        )
    }

    suspend fun listOrders(
        token: String,
        userId: String? = null
    ): List<OrderResponseDto> {
        val auth = "Bearer $token"
        return api.listOrders(
            authHeader = auth,
            userIdHeader = userId
        )
    }

    suspend fun getOrder(
        token: String,
        orderId: String,
        userId: String? = null
    ): OrderResponseDto {
        val auth = "Bearer $token"
        return api.getOrder(
            orderId = orderId,
            authHeader = auth,
            userIdHeader = userId
        )
    }
}
