package com.milsabores.appkotlin_guia

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.milsabores.appkotlin_guia.model.EstadoDataStore
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.navigation.NavigationEvent
import com.milsabores.appkotlin_guia.ui.screens.*
import com.milsabores.appkotlin_guia.ui.theme.AppKotlin_GuiaTheme
import com.milsabores.appkotlin_guia.viewmodel.*
import com.milsabores.appkotlin_guia.repository.AppDataBase
import com.milsabores.appkotlin_guia.repository.CartRepository
import com.milsabores.appkotlin_guia.repository.OrderRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // DataStore y Database
        val prefs = EstadoDataStore(applicationContext)
        val db = androidx.room.Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "milsabores-db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val cartRepo = CartRepository(db.cartDao())
        val orderRepo = OrderRepository(db.orderDao())

        setContent {
            AppKotlin_GuiaTheme {
                // ViewModels compartidos
                val mainVm: MainViewModel = viewModel()
                val usuarioVm: UsuarioViewModel = viewModel()
                val estadoVm: EstadoViewModel = viewModel()
                val catalogVm: CatalogViewModel = viewModel()

                // CartViewModel con ambos repositorios (sin Factory)
                val cartVm: CartViewModel = viewModel(
                    factory = CartViewModelFactory(application)
                )


                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                val userEmail by prefs.userEmail.collectAsState(initial = null)

                // Flags de DataStore
                val onboardingDone by prefs.onboardingDone.collectAsState(initial = false)
                val guestMode by prefs.guestMode.collectAsState(initial = false)
                val isLoggedIn by prefs.isLoggedIn.collectAsState(initial = false)

                // Carrito global (para badge)
                val cartUi by cartVm.ui.collectAsState()
                val cartCount = remember(cartUi.items) { cartUi.items.sumOf { it.quantity } }

                // Navegación desde el ViewModel
                LaunchedEffect(Unit) {
                    mainVm.navEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.appRoute.route) {
                                    event.popUpRoute?.let {
                                        popUpTo(it.route) {
                                            inclusive = event.inclusive
                                        }
                                    }
                                    launchSingleTop = event.singleTop
                                    restoreState = true
                                }
                            }
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppRoute.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // 1. Splash
                        composable(AppRoute.Splash.route) {
                            SplashScreen(
                                onFinish = {
                                    navController.navigate(AppRoute.Onboarding.route) {
                                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. Onboarding
                        composable(AppRoute.Onboarding.route) {
                            OnboardingScreen(
                                onSkipClick = {
                                    scope.launch { prefs.setOnboardingDone(true) }
                                    navController.navigate(AppRoute.Entry.route) {
                                        popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                                    }
                                },
                                onFinishClick = {
                                    scope.launch { prefs.setOnboardingDone(true) }
                                    navController.navigate(AppRoute.Entry.route) {
                                        popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 3. Entry (Invitado / Login)
                        composable(AppRoute.Entry.route) {
                            EntryScreen(
                                isLogged = isLoggedIn,
                                onGuestClick = {
                                    scope.launch {
                                        prefs.setGuestMode(true)
                                        prefs.setLoggedIn(false)
                                    }
                                    navController.navigate(AppRoute.Home.route) {
                                        popUpTo(AppRoute.Entry.route) { inclusive = true }
                                    }
                                },
                                onLoginClick = {
                                    navController.navigate(AppRoute.Login.route)
                                },
                                onRegisterClick = {
                                    navController.navigate(AppRoute.Register.route)
                                }
                            )
                        }

                        // 4. Login
                        composable(AppRoute.Login.route) {
                            LoginScreen(
                                navController = navController,
                                usuarioVm = usuarioVm,
                                prefs = prefs
                            )
                        }

                        // 5. Home
                        composable(AppRoute.Home.route) {
                            HomeScreen(
                                viewModel = mainVm,
                                navController = navController,
                                cartVm = cartVm,
                                catalogVm = catalogVm,
                                isLoggedIn = isLoggedIn
                            )
                        }

                        // Registro
                        composable(AppRoute.Register.route) {
                            RegistroScreen(
                                viewModel = usuarioVm,
                                navController = navController
                            )
                        }

                        // Perfil
                        composable(AppRoute.Profile.route) {
                            ProfileScreen(
                                navController = navController,
                                mainVm = mainVm,
                                prefs = prefs,
                                cartCount = cartCount,
                                isLoggedInOverride = isLoggedIn
                            )
                        }

                        // Estado
                        composable(AppRoute.Estado.route) {
                            PantallaEstado(viewModel = estadoVm)
                        }

                        // Resumen
                        composable(AppRoute.Resumen.route) {
                            ResumenScreen(usuarioVm)
                        }

                        // Menu
                        composable(AppRoute.Menu.route) {
                            MenuScreen(
                                navController = navController,
                                catalogVm = catalogVm
                            )
                        }

                        // API EXTERNA
                        composable(AppRoute.DemoApi.route) {
                            DemoApiScreen()
                        }

                        // Carrito
                        composable(AppRoute.Cart.route) {
                            CartScreen(
                                navController = navController,
                                vm = cartVm,
                                isGuest = !isLoggedIn,
                                onLoginRequested = {
                                    navController.navigate(AppRoute.Login.route)
                                }
                            )
                        }

                        // Checkout
                        composable(AppRoute.Checkout.route) {
                            CheckoutScreen(
                                navController = navController,
                                cartVm = cartVm
                            )
                        }

                        // Checkout Success
                        composable(AppRoute.CheckoutSuccess.route) {
                            CheckoutSuccessScreen(navController)
                        }

                        // Detalle de producto product/{id}
                        composable(
                            route = AppRoute.Product.route,
                            arguments = listOf(
                                navArgument(AppRoute.Product.ARG_ID) {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val productId =
                                backStackEntry.arguments?.getString(AppRoute.Product.ARG_ID)
                                    ?: return@composable
                            ProductDetailScreen(
                                navController = navController,
                                productId = productId,
                                cartVm = cartVm,
                                isLoggedIn = isLoggedIn,
                                catalogVm = catalogVm
                            )
                        }
                    }
                }
            }
        }
    }
}

class CartViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            val db = AppDataBase.getInstance(app)
            val cartRepo = CartRepository(db.cartDao())
            val orderRepo = OrderRepository(db.orderDao())

            // 👇 AQUÍ simplemente retornas la instancia con ambos repos
            return CartViewModel(
                repo = cartRepo,
                orderRepo = orderRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
