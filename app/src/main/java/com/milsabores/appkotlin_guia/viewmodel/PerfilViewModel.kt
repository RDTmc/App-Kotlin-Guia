package com.milsabores.appkotlin_guia.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PerfilViewModel(app: Application) : AndroidViewModel(app) {

    private val _fotoUri = MutableStateFlow<Uri?>(null)
    val fotoUri: StateFlow<Uri?> = _fotoUri

    // Galeria
    fun setFromGallery(uri: Uri) {
        _fotoUri.value = uri
    }

    // Camara: crea un archivo temporal y devuelve su Uri para TakePicture()
    fun createTempImageUri(): Uri {
        val ctx: Context = getApplication()
        val imagesDir = File(ctx.cacheDir, "images").apply { mkdirs() }
        val imageFile = File.createTempFile("captura_", ".jpg", imagesDir)
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", imageFile)
    }

    // Camara: confirmar captura exitosa
    fun setFromCamera(success: Boolean, outputUri: Uri?) {
        if (success) _fotoUri.value = outputUri
    }

    // limpiar
    fun clear() { viewModelScope.launch { _fotoUri.value = null } }
}