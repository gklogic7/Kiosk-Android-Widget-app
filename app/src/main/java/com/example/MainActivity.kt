package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KioskDesktopScreen
import com.example.viewmodel.KioskViewModel
import com.example.widget.KioskWidgetUpdater

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Sync standalone Home Screen widgets on launch
        KioskWidgetUpdater.updateAllWidgets(applicationContext)
        setContent {
            val kioskViewModel: KioskViewModel = viewModel()
            KioskDesktopScreen(viewModel = kioskViewModel)
        }
    }
}
