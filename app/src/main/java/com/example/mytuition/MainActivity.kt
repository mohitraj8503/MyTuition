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
    enableEdgeToEdge()
    setContent {
      MyTuitionTheme {
        AppNavGraph()
      }
    }
  }
}
