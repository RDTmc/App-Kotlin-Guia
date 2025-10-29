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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
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
import com.milsabores.appkotlin_guia.ui.screens.*
import com.milsabores.appkotlin_guia.ui.screens.HomeScreen
import com.milsabores.appkotlin_guia.ui.screens.PantallaEstado
import com.milsabores.appkotlin_guia.ui.screens.ProfileScreen
import com.milsabores.appkotlin_guia.ui.screens.RegistroScreen
import com.milsabores.appkotlin_guia.ui.screens.ResumenScreen
import com.milsabores.appkotlin_guia.ui.screens.UsuariosListScreen
import com.milsabores.appkotlin_guia.ui.theme.AppKotlin_GuiaTheme
import com.milsabores.appkotlin_guia.viewmodel.EstadoViewModel
import com.milsabores.appkotlin_guia.viewmodel.MainViewModel
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = EstadoDataStore(this)

        setContent {
            AppKotlin_GuiaTheme{
                val viewModel: MainViewModel = viewModel()
                val viewModelRegistro: UsuarioViewModel =viewModel()
                val estadoViewModel: EstadoViewModel = viewModel()
                val navController = rememberNavController()

                // Flags de DataStore
                val onboardingDone by prefs.onboardingDone.collectAsState(initial = false)
                println("DEBUG onboardingDone=$onboardingDone")
                val guestMode by prefs.guestMode.collectAsState(initial = false)
                val scope = rememberCoroutineScope()


                LaunchedEffect(Unit) {
                    viewModel.navEvents.collectLatest {
                        event ->
                        when(event){
                            is NavigationEvent.NavigateTo ->{
                                navController.navigate(event.appRoute.route){
                                    event.popUpRoute?.let {
                                        popUpTo(it.route){
                                            inclusive=event.inclusive

                                        }
                                        launchSingleTop=event.singleTop
                                        restoreState=true
                                    }
                                }
                            }
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                            is NavigationEvent.PopBackStack -> navController.popBackStack()

                        }
                    }
                }    // Fin de LaunchedEffect, encargado de realizar la coroutina para manejar la navegación.

                Scaffold(modifier = Modifier.fillMaxSize())
                {
                        innerPadding ->
                    NavHost(
                        navController=navController,
                        startDestination = AppRoute.Splash.route,
                        modifier = Modifier.padding(innerPadding)

                    ){
                        // Layout 1: SPLASH (decide Onboarding o Entry)
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

                        // Layout 1 (opcional): ONBOARDING
                        composable(AppRoute.Onboarding.route) {
                            OnboardingScreen(
                                onSkipClick = {
                                    scope.launch { prefs.setOnboardingDone(true) }
                                    navController.navigate(AppRoute.Entry.route) {
                                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                                    }
                                },
                                onFinishClick = {
                                    scope.launch { prefs.setOnboardingDone(true) }
                                    navController.navigate(AppRoute.Entry.route) {
                                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Layout 2: ENTRY (Invitado / Login)
                        composable(AppRoute.Entry.route) {
                            EntryScreen(
                                onGuestClick = {
                                    scope.launch { prefs.setGuestMode(true) }
                                    navController.navigate(AppRoute.Home.route) {
                                        popUpTo(AppRoute.Entry.route) { inclusive = true }
                                    }
                                },
                                onLoginClick = { navController.navigate(AppRoute.Register.route) },
                                onResetOnboardingClick = {
                                    scope.launch { prefs.resetOnboarding() }
                                    navController.navigate(AppRoute.Onboarding.route) {
                                        popUpTo(AppRoute.Entry.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(AppRoute.Home.route) {
                            HomeScreen(viewModel,navController)
                        }
                        composable(AppRoute.Register.route) {
                            RegistroScreen(viewModelRegistro,navController)
                        }
                        composable(AppRoute.Profile.route) {
                            ProfileScreen(viewModel,navController)
                        }
                        composable(AppRoute.Settings.route) {
                            //SettingScreen(navController,viewModel)
                        }

                        composable(AppRoute.Resumen.route) {
                            ResumenScreen(viewModelRegistro)
                        }

                        composable(
                            route = AppRoute.Product.route,
                            arguments = listOf(navArgument(AppRoute.Product.ARG_ID) { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString(AppRoute.Product.ARG_ID) ?: return@composable
                            ProductDetailScreen(
                                navController = navController,
                                productId = productId
                            )
                        }



                        composable("usuariosTest") {
                            UsuariosListScreen(vm = viewModelRegistro)
                        }

                        composable(AppRoute.Estado.route) {
                            PantallaEstado(viewModel = estadoViewModel)
                        }
                    }
                }

            }
        }
    }
}
