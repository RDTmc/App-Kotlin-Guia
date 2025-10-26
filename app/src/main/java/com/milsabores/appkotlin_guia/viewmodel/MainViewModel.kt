package com.milsabores.appkotlin_guia.viewmodel

import androidx.lifecycle.ViewModel
import com.milsabores.appkotlin_guia.navigation.AppRoute
import com.milsabores.appkotlin_guia.navigation.NavigationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch



class MainViewModel: ViewModel() {


    private val _navEvents= MutableSharedFlow<NavigationEvent>()
    val navEvents= _navEvents.asSharedFlow()

    fun navigateTo(appRoute: AppRoute){
        CoroutineScope(Dispatchers.Main).launch {
            _navEvents.emit(NavigationEvent.NavigateTo(appRoute))
        }
    }

    fun navigateBack(){
        CoroutineScope(Dispatchers.Main).launch {
            _navEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp(){
        CoroutineScope(Dispatchers.Main).launch {
            _navEvents.emit(NavigationEvent.NavigateUp)
        }
    }




}