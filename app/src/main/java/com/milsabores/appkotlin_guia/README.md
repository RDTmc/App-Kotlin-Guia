# AppMilSabores

Aplicación móvil Android desarrollada en **Kotlin + Jetpack Compose**, 
que consume datos desde una **API REST** externa usando **Retrofit**, 
siguiendo el patrón arquitectónico **MVVM**.

Este proyecto forma parte de la **Guía 14: Consumiendo una API Rest** 
de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**. 

## 1. Requisitos

- Android Studio actualizado
- Conexión a internet
- Backend de Mil Sabores ejecutándose (`Spring Boot` en el puerto `9090`)
- Dispositivo físico o emulador Android
- Cuenta GitHub y Trello (para la parte colaborativa de la guía) 

## 2. Configuración de la API (BASE_URL)

La app consume el listado de productos desde un backend propio (API de Mil Sabores).
La configuración central de red está en:
data/remote/ApiClient.kt

Dentro de ese archivo existe el objeto NetworkConfig:

private object NetworkConfig {
// Se debe cambiar esta IP por la IP del PC en la misma red que el teléfono
const val BASE_URL = "http://192.168.1.82:9090/api/"
const val DEBUG = true
}

## ¿Qué debes modificar?

IP del PC
Reemplazar 192.168.1.82 por la IP local del PC en la misma red que el teléfono.(la que corre el backend).

Ejemplo:
const val BASE_URL = "http://192.168.0.10:9090/api/"

## Mismo WiFi

El teléfono/emulador debe estar en la misma red que el PC para poder acceder a esa IP.
Puerto y contexto

Por defecto se usa 9090 y el contexto /api/.
Si el backend cambia de puerto o contexto, ajustar también:

const val BASE_URL = "http://<IP>:<PUERTO>/<contexto>/"

## Estructura del proyecto (MVVM + Retrofit + Compose)

La estructura respeta lo solicitado en la guía (ui/screens, viewmodel, data/model, data/remote, repository).

.
├── MainActivity.kt
├── data
│   └── remote
│       ├── ApiClient.kt           # Configuración Retrofit + OkHttp + Moshi
│       ├── CatalogRemoteRepository.kt
│       └── dto
│           └── ProductsDto.kt     # DTO + mappers a Product
├── model
│   ├── Product.kt                 # Modelo de dominio
│   ├── CartItem.kt
│   └── ...
├── repository
│   ├── AppDataBase.kt
│   ├── CartRepository.kt
│   └── OrderRepository.kt
├── ui
│   ├── screens
│   │   ├── EntryScreen.kt         # Pantalla de entrada + demo consumo API
│   │   ├── HomeScreen.kt
│   │   ├── MenuScreen.kt
│   │   └── ...
│   ├── components
│   └── theme
└── viewmodel
├── CatalogViewModel.kt        # MVVM para el catálogo (API REST)
├── ProductDetailViewModel.kt
└── ...

## Flujo de datos (simplificado)

1. ApiClient crea una instancia de Retrofit apuntando a BASE_URL.

2. ApiService define los endpoints de la API (GET /products, GET /products/{id}, etc.).

3. CatalogRemoteRepository invoca a ApiService y mapea los DTO a Product.

4. CatalogViewModel expone un StateFlow<CatalogUiState> con:

products
featured
isLoading
error

5. EntryScreen (y otras pantallas) observan ese estado y muestran la información en Compose.

## Uso de la app (Pasos rápidos)

1. Clonar el repositorio:
https://github.com/RDTmc/App-Kotlin-Guia
2. Abrir el proyecto en Android Studio.
3. Editar la BASE_URL en data/remote/ApiClient.kt para que apunte a la IP de tu backend.
4. Levantar el backend de Mil Sabores (Spring Boot) en el puerto configurado (por defecto 9090).
5. Conectar el dispositivo físico o configurar un emulador.
6. Ejecutar la app desde Android Studio.
7. En la EntryScreen, usar el botón:
   - Ver demo consumo API REST (Productos): 
   - Invitado: 
   - Iniciar sesión: 
   - Crear cuenta:
   - Para abrir un modal que lista todos los productos obtenidos desde la API real.
   
## Resultados según Guía 14
- Parte 1 – Configuración del proyecto Compose con MVVM

Proyecto creado con Empty Compose Activity.
Estructura de paquetes:

ui/screens
viewmodel
data/model, data/remote
repository

- Parte 2 – Configuración de librerías y dependencias

Se agregaron dependencias de:
Retrofit + MoshiConverterFactory
OkHttp (con HttpLoggingInterceptor en modo debug)
Corrutinas (para uso en viewModelScope).
Proyecto sincronizado y ejecutado sin errores. 

- Parte 3 – Entorno colaborativo (GitHub)

Repositorio publico en GitHub con nombre del proyecto.
Colaboradores agregados:
- 27diegorojasduoc
- MoixesMasc

Primer commit con mensaje:
- Inicio de proyecto + estructura base para consumo de API REST

- Parte 4 – Desarrollo de la funcionalidad con Retrofit y Jetpack Compose
1. Se añadió el permiso de internet en AndroidManifest.xml:
- <uses-permission android:name="android.permission.INTERNET" />
2. Se implementó el consumo de una API REST real (backend de Mil Sabores) mediante:

ApiClient.kt (equivalente a RetrofitInstance de la guía).
ApiService (interface con endpoints /api/products).
CatalogRemoteRepository (equivalente a PostRepository).
CatalogViewModel (equivalente a PostViewModel).
EntryScreen + modal de demo (equivalente a PostScreen).
La aplicación se ejecuta y muestra el listado de productos obtenidos desde la API, 
demostrando el consumo de datos en una pantalla desarrollada con Jetpack Compose.

Otros métodos HTTP
La arquitectura está preparada para extender ApiService 
con métodos POST, PUT y DELETE sobre el recurso de productos, 
pero no se implementaron en esta guía.
Ejemplo:

interface ApiService {
@POST("products")
suspend fun createProduct(@Body product: ProductDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: ProductDto
    ): ProductDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)
}

