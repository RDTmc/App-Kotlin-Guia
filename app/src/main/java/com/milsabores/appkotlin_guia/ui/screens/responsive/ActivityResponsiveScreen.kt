package com.milsabores.appkotlin_guia.ui.screens.responsive

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.milsabores.appkotlin_guia.ui.util.findActivity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ActivityResponsiveScreen() {
    val ctx = LocalContext.current
    val act = ctx.findActivity()

    val widthClass = if (act != null) {
        // ✅ Solo calculamos si tenemos Activity real
        calculateWindowSizeClass(act).widthSizeClass
    } else {
        // ✅ Fallback seguro para evitar crash
        WindowWidthSizeClass.Compact
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Actividad Adaptable") }) }
    ) { padding ->
        when (widthClass) {
            WindowWidthSizeClass.Compact  -> ActivityCompact(padding)
            WindowWidthSizeClass.Medium   -> ActivityMedium(padding)
            WindowWidthSizeClass.Expanded -> ActivityExpanded(padding)
            else -> ActivityCompact(padding)
        }
    }
}

/* ===== PREVIEWS ===== */
@Preview(showBackground = true, name = "Compact")
@Composable
private fun ActivityCompactPreview() { ActivityCompact() }

@Preview(showBackground = true, widthDp = 700, name = "Medium")
@Composable
private fun ActivityMediumPreview() { ActivityMedium() }

@Preview(showBackground = true, widthDp = 1000, name = "Expanded")
@Composable
private fun ActivityExpandedPreview() { ActivityExpanded() }
