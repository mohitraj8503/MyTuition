package com.example.mytuition.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.MyTuitionLogo
import com.example.mytuition.core.di.AppContainer
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit = onNavigateToLogin,
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModel.provideFactory(AppContainer.authRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var animationTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationTriggered = true
        delay(1400) // allow splash animation to play smoothly
        when (state) {
            is SplashState.NavigateToHome -> onNavigateToHome()
            is SplashState.NavigateToOnboarding -> onNavigateToLogin()
            is SplashState.NavigateToLogin -> onNavigateToLogin()
            SplashState.Loading -> {
                onNavigateToLogin()
            }
        }
    }

    val logoScale by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 260f),
        label = "splashLogoScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(500),
        label = "splashContentAlpha"
    )

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Graduation cap logo in white clay circle card (140.dp) with scale-in + fade-in
                MyTuitionLogo(
                    size = 140.dp,
                    showClayCard = true,
                    modifier = Modifier
                        .scale(logoScale)
                        .alpha(contentAlpha)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // App Name: "MyTuition" (32sp Bold PrimaryPurple)
                Text(
                    text = "MyTuition",
                    style = MyTuitionTypography.HeadlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.PrimaryPurple
                    ),
                    modifier = Modifier.alpha(contentAlpha)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tagline: "Learn • Teach • Grow Together" (15sp TextSecondary)
                Text(
                    text = "Learn • Teach • Grow Together",
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MyTuitionColors.TextSecondary
                    ),
                    modifier = Modifier.alpha(contentAlpha)
                )
            }

            // Bottom animated progress indicator dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "dotsTransition")
                val dot1Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot1"
                )
                val dot2Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 200),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot2"
                )
                val dot3Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 400),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot3"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MyTuitionColors.PrimaryPurple.copy(alpha = dot1Alpha))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MyTuitionColors.PrimaryPurple.copy(alpha = dot2Alpha))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MyTuitionColors.PrimaryPurple.copy(alpha = dot3Alpha))
                )
            }
        }
    }
}
