package com.milsabores.appkotlin_guia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.navigation.NavigationEvent
import com.milsabores.appkotlin_guia.ui.screens.HomeScreen
import com.milsabores.appkotlin_guia.ui.screens.ProfileScreen
import com.milsabores.appkotlin_guia.ui.screens.RegistroScreen
import com.milsabores.appkotlin_guia.ui.screens.ResumenScreen
import com.milsabores.appkotlin_guia.ui.theme.AppKotlin_GuiaTheme
import com.milsabores.appkotlin_guia.viewmodel.MainViewModel
import com.milsabores.appkotlin_guia.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppKotlin_GuiaTheme{
                val viewModel: MainViewModel = viewModel()
                val viewModelRegistro: UsuarioViewModel =viewModel()
                val navController = rememberNavController()

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
                }    //finaliza el bloque LaunchedEffect, encargado de realizar la coroutina para manejar la navegación.

                Scaffold(modifier = Modifier.fillMaxSize())
                {
                        innerPadding ->
                    NavHost(
                        navController=navController,
                        startDestination = AppRoute.Register.route,
                        modifier = Modifier.padding(innerPadding)

                    ){
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
                    }
                }

            }
        }
    }
}
