package com.milsabores.appkotlin_guia

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.milsabores.appkotlin_guia.model.EstadoDataStore
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.navigation.NavigationEvent
import com.milsabores.appkotlin_guia.ui.screens.CartScreen
import com.milsabores.appkotlin_guia.ui.screens.CheckoutScreen
import com.milsabores.appkotlin_guia.ui.screens.EntryScreen
import com.milsabores.appkotlin_guia.ui.screens.HomeScreen
import com.milsabores.appkotlin_guia.ui.screens.OnboardingScreen
import com.milsabores.appkotlin_guia.ui.screens.PantallaEstado
import com.milsabores.appkotlin_guia.ui.screens.ProfileScreen
import com.milsabores.appkotlin_guia.ui.screens.RegistroScreen
import com.milsabores.appkotlin_guia.ui.screens.ResumenScreen
import com.milsabores.appkotlin_guia.ui.screens.SplashScreen
import com.milsabores.appkotlin_guia.ui.screens.UsuariosListScreen
import com.milsabores.appkotlin_guia.ui.screens.ProductDetailScreen
import com.milsabores.appkotlin_guia.ui.theme.AppKotlin_GuiaTheme
import com.milsabores.appkotlin_guia.viewmodel.CartViewModel
import com.milsabores.appkotlin_guia.viewmodel.CatalogViewModel
import com.milsabores.appkotlin_guia.viewmodel.EstadoViewModel
import com.milsabores.appkotlin_guia.viewmodel.MainViewModel
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataStore fuera del compose
        val prefs = EstadoDataStore(applicationContext)
        val db = androidx.room.Room.databaseBuilder(
            applicationContext,
            com.milsabores.appkotlin_guia.repository.AppDataBase::class.java,
            "milsabores-db"
        ).fallbackToDestructiveMigration().build()
        val cartRepo = com.milsabores.appkotlin_guia.repository.CartRepository(db.cartDao())


        setContent {
            AppKotlin_GuiaTheme {
                // VMs compartidos
                val mainVm: MainViewModel = viewModel()
                val usuarioVm: UsuarioViewModel = viewModel()
                val estadoVm: EstadoViewModel = viewModel()
                val cartVm: CartViewModel = viewModel(
                    factory = androidx.lifecycle.viewmodel.initializer{
                        CartViewModel(cartRepo)
                    }
                )
                val catalogVm: CatalogViewModel = viewModel()

                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                // Flags de DataStore
                val onboardingDone by prefs.onboardingDone.collectAsState(initial = false)
                val guestMode by prefs.guestMode.collectAsState(initial = false)

                // carrito global (para badge, si lo quieres en algún layout root)
                val cartUi by cartVm.ui.collectAsState()
                val cartCount = cartUi.items.sumOf { it.quantity }  // <- ahora sí existe


                // Navegación desde el VM
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
                        // si ya vio onboarding, lo mandamos a Entry; si no, a Splash
                        startDestination = if (onboardingDone) AppRoute.Entry.route else AppRoute.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // 1. Splash
                        composable(AppRoute.Splash.route) {
                            SplashScreen(
                                onFinish = {
                                    if (onboardingDone) {
                                        navController.navigate(AppRoute.Entry.route) {
                                            popUpTo(AppRoute.Splash.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(AppRoute.Onboarding.route) {
                                            popUpTo(AppRoute.Splash.route) { inclusive = true }
                                        }
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
                                onGuestClick = {
                                    scope.launch { prefs.setGuestMode(true) }
                                    navController.navigate(AppRoute.Home.route) {
                                        popUpTo(AppRoute.Entry.route) { inclusive = true }
                                    }
                                },
                                onLoginClick = {
                                    navController.navigate(AppRoute.Register.route)
                                },
                                onResetOnboardingClick = {
                                    scope.launch { prefs.resetOnboarding() }
                                    navController.navigate(AppRoute.Onboarding.route) {
                                        popUpTo(AppRoute.Entry.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 4. Home
                        composable(AppRoute.Home.route) {
                            HomeScreen(
                                viewModel = mainVm,
                                navController = navController,
                                cartVm = cartVm,
                                catalogVm = catalogVm
                            )
                        }

                        // Registro
                        composable(AppRoute.Register.route) {
                            RegistroScreen(
                                viewModel = usuarioVm,
                                navController = navController)
                        }

                        // Perfil
                        composable(AppRoute.Profile.route) {
                            ProfileScreen(mainVm, navController)
                        }

                        // Estado
                        composable(AppRoute.Estado.route) {
                            PantallaEstado(viewModel = estadoVm)
                        }

                        // Resumen
                        composable(AppRoute.Resumen.route) {
                            ResumenScreen(usuarioVm)
                        }

                        // Carrito
                        composable(AppRoute.Cart.route) {
                            CartScreen(
                                navController = navController,
                                vm = cartVm,
                                isGuest = guestMode,
                                onLoginRequested = { navController.navigate(AppRoute.Register.route) }
                            )
                        }

                        composable(AppRoute.Checkout.route) {
                            CheckoutScreen(
                                navController = navController,
                                cartVm = cartVm
                            )
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
                                backStackEntry.arguments?.getString(AppRoute.Product.ARG_ID) ?: return@composable
                            ProductDetailScreen(
                                navController = navController,
                                productId = productId,
                                cartVm = cartVm,
                                catalogVm = catalogVm
                            )
                        }

                        // prueba
                        composable("usuariosTest") {
                            UsuariosListScreen(vm = usuarioVm)
                        }
                    }
                }
            }
        }
    }
}
