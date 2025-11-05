package com.milsabores.appkotlin_guia.ui.util

import android.content.Context

fun resIdFor(context: Context, assetPath: String?): Int {
    if (assetPath.isNullOrBlank()) return 0

    val name = assetPath
        .substringAfterLast('/')   // img/tt_vainilla.png -> tt_vainilla.png
        .substringBeforeLast('.')  // tt_vainilla.png -> tt_vainilla

    if (name.isBlank()) return 0

    return context.resources.getIdentifier(name, "drawable", context.packageName)
}


