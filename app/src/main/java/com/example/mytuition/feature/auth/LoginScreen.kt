package com.example.mytuition.feature.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.di.AppContainer

val LimeInput = Color(0xFFA8E600)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.provideFactory(AppContainer.authRepository)
    )
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is LoginUiState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // Animation for entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val transition = updateTransition(targetState = visible, label = "Entrance")
    val panelScale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = 0.6f, stiffness = 200f) },
        label = "Panel Scale"
    ) { if (it) 1f else 0.9f }
    val panelAlpha by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "Panel Alpha"
    ) { if (it) 1f else 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.WarmIvory)
    ) {
        // Decorative Elements
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 60.dp)
                .size(160.dp)
                .background(MyTuitionColors.PremiumPurple, CircleShape)
                .graphicsLayer { alpha = 0.6f }
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-20).dp)
                .size(200.dp)
                .background(MyTuitionColors.PremiumBlue, CircleShape)
                .graphicsLayer { alpha = 0.6f }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = 40.dp)
                .size(120.dp)
                .background(MyTuitionColors.PremiumCoral, CircleShape)
                .graphicsLayer { alpha = 0.5f }
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .graphicsLayer {
                    scaleX = panelScale
                    scaleY = panelScale
                    alpha = panelAlpha
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 32.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color.Black.copy(alpha = 0.05f),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .background(MyTuitionColors.PremiumLime, RoundedCornerShape(40.dp))
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(40.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mascot Head
                    MyTuitionMascot(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    
                    Text(
                        text = "Ready for focus?",
                        style = MyTuitionTypography.Display.copy(
                            fontSize = 32.sp
                        ),
                        color = MyTuitionColors.DeepNavyText
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Everything you need for tuition,\nall in one place.",
                        style = MyTuitionTypography.Body.copy(
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        ),
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    if (state is LoginUiState.Error) {
                        Text(
                            text = (state as LoginUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                    }

                    // Google Button
                    val isGoogleLoading = state is LoginUiState.Loading && (state as LoginUiState.Loading).provider == AuthProvider.GOOGLE
                    MyTuitionAuthButton(
                        text = if (isGoogleLoading) "Signing in with Google..." else "Continue with Google",
                        icon = { GoogleIcon() },
                        onClick = { viewModel.signInWithGoogle() },
                        isLoading = isGoogleLoading,
                        enabled = state is LoginUiState.Idle || state is LoginUiState.Error,
                        containerColor = Color.White,
                        contentColor = MyTuitionColors.DeepNavyText,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // GitHub Button
                    val isGithubLoading = state is LoginUiState.Loading && (state as LoginUiState.Loading).provider == AuthProvider.GITHUB
                    MyTuitionAuthButton(
                        text = if (isGithubLoading) "Signing in with GitHub..." else "Continue with GitHub",
                        icon = { GitHubIcon() },
                        onClick = { viewModel.signInWithGitHub() },
                        isLoading = isGithubLoading,
                        enabled = state is LoginUiState.Idle || state is LoginUiState.Error,
                        containerColor = MyTuitionColors.DeepNavyText,
                        contentColor = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.4f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Demo Button
                    val isDemoLoading = state is LoginUiState.Loading && (state as LoginUiState.Loading).provider == AuthProvider.DEMO
                    MyTuitionAuthButton(
                        text = if (isDemoLoading) "Entering demo..." else "Enter Demo Mode",
                        icon = null,
                        onClick = { viewModel.enterDemoMode() },
                        isLoading = isDemoLoading,
                        enabled = state is LoginUiState.Idle || state is LoginUiState.Error,
                        containerColor = LimeInput,
                        contentColor = MyTuitionColors.DeepNavyText,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "Need help?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { 
                            // Hidden demo entry can go here if requested
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyTuitionAuthButton(
    text: String,
    icon: @Composable (() -> Unit)?,
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "ButtonScale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = if (isPressed && enabled) 2.dp else 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MyTuitionColors.DeepNavyText.copy(alpha = 0.4f),
                ambientColor = MyTuitionColors.DeepNavyText.copy(alpha = 0.2f)
            )
            .background(containerColor, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = contentColor, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                Spacer(modifier = Modifier.width(12.dp))
            } else if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GoogleIcon() {
    val vector = remember {
        androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "Google",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(Color(0xFF4285F4))) {
                moveTo(22.56f, 12.25f)
                curveTo(22.56f, 11.47f, 22.49f, 10.73f, 22.36f, 10.0f)
                lineTo(12.0f, 10.0f)
                lineTo(12.0f, 14.26f)
                lineTo(17.92f, 14.26f)
                curveTo(17.66f, 15.63f, 16.88f, 16.78f, 15.74f, 17.54f)
                lineTo(15.74f, 20.3f)
                lineTo(19.31f, 20.3f)
                curveTo(21.39f, 18.38f, 22.56f, 15.6f, 22.56f, 12.25f)
                close()
            }
            path(fill = androidx.compose.ui.graphics.SolidColor(Color(0xFF34A853))) {
                moveTo(12.0f, 23.0f)
                curveTo(14.97f, 23.0f, 17.46f, 22.01f, 19.31f, 20.3f)
                lineTo(15.74f, 17.54f)
                curveTo(14.74f, 18.21f, 13.48f, 18.62f, 12.0f, 18.62f)
                curveTo(9.13f, 18.62f, 6.7f, 16.68f, 5.82f, 14.07f)
                lineTo(2.15f, 14.07f)
                lineTo(2.15f, 16.92f)
                curveTo(3.97f, 20.54f, 7.68f, 23.0f, 12.0f, 23.0f)
                close()
            }
            path(fill = androidx.compose.ui.graphics.SolidColor(Color(0xFFFBBC05))) {
                moveTo(5.82f, 14.07f)
                curveTo(5.59f, 13.4f, 5.46f, 12.71f, 5.46f, 12.0f)
                curveTo(5.46f, 11.29f, 5.59f, 10.6f, 5.82f, 9.93f)
                lineTo(2.15f, 9.93f)
                lineTo(2.15f, 7.08f)
                curveTo(1.4f, 8.57f, 1.0f, 10.23f, 1.0f, 12.0f)
                curveTo(1.0f, 13.77f, 1.4f, 15.43f, 2.15f, 16.92f)
                lineTo(5.82f, 14.07f)
                close()
            }
            path(fill = androidx.compose.ui.graphics.SolidColor(Color(0xFFEA4335))) {
                moveTo(12.0f, 5.38f)
                curveTo(13.62f, 5.38f, 15.08f, 5.93f, 16.22f, 7.02f)
                lineTo(19.39f, 3.85f)
                curveTo(17.46f, 2.05f, 14.97f, 1.0f, 12.0f, 1.0f)
                curveTo(7.68f, 1.0f, 3.97f, 3.46f, 2.15f, 7.08f)
                lineTo(5.82f, 9.93f)
                curveTo(6.7f, 7.32f, 9.13f, 5.38f, 12.0f, 5.38f)
                close()
            }
        }.build()
    }
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = vector, contentDescription = "Google", modifier = Modifier.size(18.dp), tint = Color.Unspecified)
    }
}

@Composable
fun GitHubIcon() {
    val vector = remember {
        androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "GitHub",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(Color.White)) {
                moveTo(12.0f, 2.0f)
                curveTo(6.477f, 2.0f, 2.0f, 6.477f, 2.0f, 12.0f)
                curveTo(2.0f, 16.42f, 4.865f, 20.166f, 8.839f, 21.489f)
                curveTo(9.339f, 21.581f, 9.521f, 21.272f, 9.521f, 21.007f)
                curveTo(9.521f, 20.77f, 9.513f, 20.141f, 9.508f, 19.307f)
                curveTo(6.726f, 19.91f, 6.139f, 17.967f, 6.139f, 17.967f)
                curveTo(5.685f, 16.811f, 5.029f, 16.505f, 5.029f, 16.505f)
                curveTo(4.121f, 15.885f, 5.098f, 15.897f, 5.098f, 15.897f)
                curveTo(6.101f, 15.967f, 6.629f, 16.927f, 6.629f, 16.927f)
                curveTo(7.521f, 18.456f, 8.97f, 18.014f, 9.539f, 17.758f)
                curveTo(9.631f, 17.112f, 9.889f, 16.672f, 10.175f, 16.422f)
                curveTo(7.955f, 16.169f, 5.62f, 15.312f, 5.62f, 11.469f)
                curveTo(5.62f, 10.378f, 6.01f, 9.485f, 6.649f, 8.786f)
                curveTo(6.546f, 8.533f, 6.203f, 7.516f, 6.747f, 6.139f)
                curveTo(6.747f, 6.139f, 7.587f, 5.87f, 9.497f, 7.164f)
                curveTo(10.296f, 6.942f, 11.151f, 6.832f, 12.001f, 6.828f)
                curveTo(12.85f, 6.832f, 13.705f, 6.942f, 14.505f, 7.164f)
                curveTo(16.415f, 5.87f, 17.253f, 6.139f, 17.253f, 6.139f)
                curveTo(17.799f, 7.516f, 17.456f, 8.533f, 17.353f, 8.786f)
                curveTo(17.993f, 9.485f, 18.381f, 10.378f, 18.381f, 11.469f)
                curveTo(18.381f, 15.311f, 16.044f, 16.156f, 13.817f, 16.404f)
                curveTo(14.176f, 16.713f, 14.495f, 17.323f, 14.495f, 18.256f)
                curveTo(14.495f, 19.592f, 14.483f, 20.671f, 14.483f, 21.007f)
                curveTo(14.483f, 21.274f, 14.663f, 21.585f, 15.171f, 21.487f)
                curveTo(19.143f, 20.16f, 22.0f, 16.416f, 22.0f, 12.0f)
                curveTo(22.0f, 6.477f, 17.523f, 2.0f, 12.0f, 2.0f)
                close()
            }
        }.build()
    }
    Icon(imageVector = vector, contentDescription = "GitHub", modifier = Modifier.size(24.dp), tint = Color.Unspecified)
}

@Composable
fun MyTuitionMascot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        // Small heart/sparkle above
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 24.dp, x = (-20).dp)
                .size(16.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .graphicsLayer { rotationZ = -15f }
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 20.dp, x = (-10).dp)
                .size(12.dp)
                .background(Color.White, RoundedCornerShape(6.dp))
                .graphicsLayer { rotationZ = -15f }
        )

        // Eyes
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            MascotEye()
            MascotEye()
        }

        // Nostrils
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).background(MyTuitionColors.DeepNavyText, CircleShape))
            Box(modifier = Modifier.size(6.dp).background(MyTuitionColors.DeepNavyText, CircleShape))
        }

        // Eyebrows
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-50).dp, y = (-40).dp)
                .size(width = 32.dp, height = 8.dp)
                .background(MyTuitionColors.DeepNavyText, RoundedCornerShape(4.dp))
                .graphicsLayer { rotationZ = -10f }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 50.dp, y = (-40).dp)
                .size(width = 32.dp, height = 8.dp)
                .background(MyTuitionColors.DeepNavyText, RoundedCornerShape(4.dp))
                .graphicsLayer { rotationZ = 10f }
        )
    }
}

@Composable
fun MascotEye() {
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 90.dp)
            .background(Color.White, RoundedCornerShape(40.dp))
            .border(
                width = 3.dp, 
                color = MyTuitionColors.DeepNavyText.copy(alpha = 0.05f), 
                shape = RoundedCornerShape(40.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Pupil
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MyTuitionColors.DeepNavyText, CircleShape)
                .offset(x = 2.dp, y = 4.dp)
        ) {
            // Highlight
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            )
            // Secondary small highlight
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    .align(Alignment.BottomStart)
                    .offset(x = 12.dp, y = (-12).dp)
            )
        }
    }
}
