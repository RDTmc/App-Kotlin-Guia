# Mil Sabores – App Móvil (Microservicios + Spring Boot)

Este proyecto es la app móvil de **Pastelería Mil Sabores**, integrada a un backend basado en microservicios Spring Boot:

- `ms-productos` (8081) – Catálogo de productos
- `ms-usuarios` (8082) – Registro, login y JWT

## Stack técnico (Android)

- Kotlin + Jetpack Compose + Material 3
- Arquitectura MVVM
- Room (carrito / pedidos locales)
- DataStore (estado de sesión + token JWT)
- Retrofit + Moshi (consumo de microservicios y API externa)

## Integraciones clave

- Login remoto contra `ms-usuarios` (`/api/auth/login`)
- Catálogo desde `ms-productos` (`/api/products`)
- API externa de postres (`TheMealDB`) como ejemplo de consumo de API pública
- Pruebas unitarias de lógica de validación (PasswordValidatorTest)
- APK firmado generado con Android Studio (Build > Generate Signed Bundle / APK)

## Ejecución

1. Levantar los microservicios en local:
   - Postgres Docker + `ms-productos`, `ms-usuarios`.
2. Actualizar la IP del backend en:
   - `ApiClient.kt` (ms-productos)
   - `AuthRemoteRepository` (`AuthNetworkConfig` (ms-usuarios))
3. Ejecutar la app en emulador o dispositivo físico.
4. Usar el usuario `clientekotlin@kotlin.cl` (u otro creado en ms-usuarios) para login.
