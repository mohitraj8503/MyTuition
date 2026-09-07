package com.example.mytuition.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.di.AppContainer

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModel.provideFactory(AppContainer.authRepository)
    )
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is SplashState.NavigateToHome -> onNavigateToHome()
            is SplashState.NavigateToLogin -> onNavigateToLogin()
            SplashState.Loading -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.WarmIvory),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MyTuition",
            style = MyTuitionTypography.Display,
            color = MyTuitionColors.PremiumPurple
        )
    }
}
