package com.example.mytuition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mytuition.core.designsystem.MyTuitionTheme
import com.example.mytuition.core.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Request highest available refresh rate (60/90/120Hz)
    try {
      val display = display
      val supportedModes = display?.supportedModes ?: emptyArray()
      val highestRefreshRateMode = supportedModes.maxByOrNull { it.refreshRate }
      highestRefreshRateMode?.let {
        window.attributes = window.attributes.apply {
          preferredDisplayModeId = it.modeId
        }
      }
    } catch (_: Exception) {
      // Fallback to default
    }

    enableEdgeToEdge()
    setContent {
      MyTuitionTheme {
        AppNavGraph()
      }
    }
  }
}
