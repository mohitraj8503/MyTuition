package com.example.mytuition.feature.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.components.MyTuitionLogo
import com.example.mytuition.core.di.AppContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.provideFactory(AppContainer.authRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneFocused by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(state) {
        if (state is LoginUiState.Success) {
            isSendingOtp = false
            onLoginSuccess()
            viewModel.resetState()
        } else if (state is LoginUiState.Error) {
            isSendingOtp = false
            coroutineScope.launch {
                for (i in 0..2) {
                    shakeOffset.animateTo(12f, tween(50))
                    shakeOffset.animateTo(-12f, tween(50))
                }
                shakeOffset.animateTo(0f, tween(50))
            }
        }
    }

    val isAnyLoading = isSendingOtp || state is LoginUiState.Loading

    // Animated Floating Background Blobs (mint + peach + light blue)
    val infiniteTransition = rememberInfiniteTransition(label = "blobMotion")
    val blob1OffsetX by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b1X"
    )
    val blob1OffsetY by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b1Y"
    )
    val blob2OffsetX by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b2X"
    )
    val blob2OffsetY by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(7500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b2Y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
    ) {
        // Living animated blobs in the background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Blob 1: Mint Green (Top Left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFB9F6CA).copy(alpha = 0.40f),
                        Color(0xFFB9F6CA).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.25f + blob1OffsetX.dp.toPx(), h * 0.18f + blob1OffsetY.dp.toPx()),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.25f + blob1OffsetX.dp.toPx(), h * 0.18f + blob1OffsetY.dp.toPx())
            )

            // Blob 2: Peach / Soft Coral (Top Right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD180).copy(alpha = 0.35f),
                        Color(0xFFFFD180).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f + blob2OffsetX.dp.toPx(), h * 0.12f + blob2OffsetY.dp.toPx()),
                    radius = w * 0.50f
                ),
                radius = w * 0.50f,
                center = Offset(w * 0.85f + blob2OffsetX.dp.toPx(), h * 0.12f + blob2OffsetY.dp.toPx())
            )

            // Blob 3: Light Sky Blue (Center / bottom)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF80D8FF).copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.50f, h * 0.50f),
                    radius = w * 0.60f
                ),
                radius = w * 0.60f,
                center = Offset(w * 0.50f, h * 0.50f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Top area: 28% height for breathing room and overlapping mascot logo
                Spacer(modifier = Modifier.weight(0.28f))

                // Bottom 72%: White Clay Card
                Box(
                    modifier = Modifier
                        .weight(0.72f)
                        .fillMaxWidth()
                ) {
                    // Card Surface
                    val cardShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                elevation = 16.dp,
                                shape = cardShape,
                                ambientColor = Color(0x221A1A1A),
                                spotColor = Color(0x181A1A1A)
                            )
                            .clip(cardShape)
                            .background(Color.White)
                            .border(2.dp, Color(0xFFEFE9FF), cardShape)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 26.dp)
                                .padding(top = 58.dp, bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Welcome Text (inside card)
                            Text(
                                text = "Welcome Back!",
                                style = MyTuitionTypography.HeadlineLarge.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF221A44)
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Sign in to continue your learning journey",
                                style = MyTuitionTypography.BodyMedium.copy(
                                    fontSize = 15.sp,
                                    color = Color(0xFF6E6A8F)
                                ),
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // PRIMARY FLOW: Phone Input Field (Clay Inset) with shake animation
                            val phoneCorner = RoundedCornerShape(20.dp)
                            val phoneBg = if (isPhoneFocused) Color(0xFFF5F2FF) else Color(0xFFF1F0F5)
                            val phoneBorder = if (isPhoneFocused) MyTuitionColors.PrimaryPurple else Color(0xFFE8E6F0)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(x = shakeOffset.value.dp)
                                    .shadow(
                                        elevation = if (isPhoneFocused) 8.dp else 0.dp,
                                        shape = phoneCorner,
                                        spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.25f),
                                        ambientColor = Color(0x106C48FF)
                                    )
                                    .clip(phoneCorner)
                                    .background(phoneBg)
                                    .border(2.dp, phoneBorder, phoneCorner)
                                    .padding(vertical = 16.dp, horizontal = 18.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Phone,
                                        contentDescription = "Phone",
                                        tint = if (isPhoneFocused) MyTuitionColors.PrimaryPurple else MyTuitionColors.TextTertiary,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "+91",
                                        style = MyTuitionTypography.LabelLarge.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF221A44)
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Box(modifier = Modifier.weight(1f)) {
                                        if (phoneNumber.isEmpty()) {
                                            Text(
                                                text = "Enter phone number",
                                                style = MyTuitionTypography.BodyMedium.copy(
                                                    fontSize = 15.sp,
                                                    color = MyTuitionColors.TextTertiary
                                                )
                                            )
                                        }
                                        BasicTextField(
                                            value = phoneNumber,
                                            onValueChange = {
                                                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                                    phoneNumber = it
                                                    localErrorMessage = null
                                                }
                                            },
                                            enabled = !isAnyLoading,
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF221A44)
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Phone,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    focusManager.clearFocus()
                                                    if (phoneNumber.length >= 10 && !isAnyLoading) {
                                                        isSendingOtp = true
                                                        coroutineScope.launch {
                                                            delay(600)
                                                            viewModel.enterDemoMode()
                                                        }
                                                    }
                                                }
                                            ),
                                            cursorBrush = SolidColor(MyTuitionColors.PrimaryPurple),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .onFocusChanged { isPhoneFocused = it.isFocused }
                                        )
                                    }
                                }
                            }

                            // Error message if any
                            val displayError = localErrorMessage ?: (state as? LoginUiState.Error)?.message
                            if (displayError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = displayError,
                                    color = MyTuitionColors.StatusRed,
                                    style = MyTuitionTypography.LabelMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Send OTP Button: Proper full-width purple clay pill button with loading state
                            val sendOtpSource = remember { MutableInteractionSource() }
                            val isSendOtpPressed by sendOtpSource.collectIsPressedAsState()
                            val sendOtpScale by animateFloatAsState(
                                targetValue = if (isSendOtpPressed && !isAnyLoading) 0.97f else 1f,
                                animationSpec = MyTuitionAnimations.claySpring,
                                label = "sendOtpScale"
                            )

                            Box(
                                modifier = Modifier
                                    .scale(sendOtpScale)
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .shadow(
                                        elevation = if (isSendOtpPressed) 4.dp else 10.dp,
                                        shape = RoundedCornerShape(28.dp),
                                        ambientColor = Color(0x336C48FF),
                                        spotColor = Color(0x226C48FF)
                                    )
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(Color(0xFF6C48FF))
                                    .border(2.dp, Color(0xFF5538CC), RoundedCornerShape(28.dp))
                                    .clickable(
                                        interactionSource = sendOtpSource,
                                        indication = null,
                                        enabled = !isAnyLoading,
                                        onClick = {
                                            focusManager.clearFocus()
                                            if (phoneNumber.length < 10) {
                                                localErrorMessage = "Please enter a valid 10-digit phone number"
                                                coroutineScope.launch {
                                                    for (i in 0..2) {
                                                        shakeOffset.animateTo(12f, tween(50))
                                                        shakeOffset.animateTo(-12f, tween(50))
                                                    }
                                                    shakeOffset.animateTo(0f, tween(50))
                                                }
                                            } else {
                                                isSendingOtp = true
                                                coroutineScope.launch {
                                                    delay(600)
                                                    viewModel.enterDemoMode()
                                                }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSendingOtp) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.5.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Sending OTP...",
                                            style = MyTuitionTypography.LabelLarge.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Send OTP",
                                            style = MyTuitionTypography.LabelLarge.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Divider: thin line + "or continue with" + thin line
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFFECECF0),
                                    thickness = 1.dp
                                )
                                Text(
                                    text = "or",
                                    style = MyTuitionTypography.LabelMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MyTuitionColors.TextTertiary
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFFECECF0),
                                    thickness = 1.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // SECONDARY FLOW: Google Button (Outlined/Secondary Style)
                            OutlinedGoogleButton(
                                isLoading = state is LoginUiState.Loading && (state as LoginUiState.Loading).provider == AuthProvider.GOOGLE,
                                enabled = !isAnyLoading,
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.signInWithGoogle()
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Demo Mode Link: "Try demo mode →" (14sp SemiBold, PrimaryPurple, Underline)
                            Text(
                                text = "Try demo mode →",
                                style = MyTuitionTypography.LabelLarge.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MyTuitionColors.PrimaryPurple,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clickable(enabled = !isAnyLoading) {
                                        focusManager.clearFocus()
                                        viewModel.enterDemoMode()
                                    }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            )

                            // 16dp spacing between Demo Mode and Sign Up
                            Spacer(modifier = Modifier.height(16.dp))

                            // Subtle Divider
                            HorizontalDivider(
                                modifier = Modifier.width(60.dp),
                                color = Color(0xFFE8E6F0),
                                thickness = 1.dp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Sign Up Link
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Don't have an account? ",
                                    style = MyTuitionTypography.BodyMedium.copy(
                                        fontSize = 15.sp,
                                        color = Color(0xFF6E6A8F)
                                    )
                                )
                                Text(
                                    text = "Sign up",
                                    style = MyTuitionTypography.BodyMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MyTuitionColors.PrimaryPurple
                                    ),
                                    modifier = Modifier
                                        .clickable(enabled = !isAnyLoading) { }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Mascot Logo: 96dp yellow cartoon mascot, centered, overlapping top card edge by -48dp
                    MyTuitionLogo(
                        size = 96.dp,
                        showClayCard = true,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-48).dp)
                    )
                }
            }
        }
    }
}

/**
 * Secondary / Outlined Google Button for reduced prominence.
 */
@Composable
private fun OutlinedGoogleButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = MyTuitionAnimations.bounceSpring,
        label = "googleScale"
    )

    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = if (isPressed) 1.dp else 3.dp,
                shape = shape,
                ambientColor = Color(0x101A1A1A),
                spotColor = Color(0x0C1A1A1A)
            )
            .clip(shape)
            .background(Color.White)
            .border(1.5.dp, Color(0xFFD8D4E6), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MyTuitionColors.PrimaryPurple,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                GoogleIconSvg(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Continue with Google",
                    style = MyTuitionTypography.LabelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF524D6E)
                    )
                )
            }
        }
    }
}

@Composable
fun GoogleIconSvg(modifier: Modifier = Modifier) {
    val vector = remember {
        ImageVector.Builder(
            name = "GoogleLogo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF4285F4))) {
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
            path(fill = SolidColor(Color(0xFF34A853))) {
                moveTo(12.0f, 23.0f)
                curveTo(14.97f, 23.0f, 17.46f, 22.02f, 19.31f, 20.3f)
                lineTo(15.74f, 17.54f)
                curveTo(14.75f, 18.2f, 13.48f, 18.61f, 12.0f, 18.61f)
                curveTo(9.13f, 18.61f, 6.7f, 16.67f, 5.83f, 14.07f)
                lineTo(2.15f, 14.07f)
                lineTo(2.15f, 16.92f)
                curveTo(3.97f, 20.53f, 7.7f, 23.0f, 12.0f, 23.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBBC05))) {
                moveTo(5.83f, 14.07f)
                curveTo(5.61f, 13.41f, 5.48f, 12.72f, 5.48f, 12.0f)
                curveTo(5.48f, 11.28f, 5.61f, 10.59f, 5.83f, 9.93f)
                lineTo(5.83f, 7.08f)
                lineTo(2.15f, 7.08f)
                curveTo(1.39f, 8.59f, 0.95f, 10.24f, 0.95f, 12.0f)
                curveTo(0.95f, 13.76f, 1.39f, 15.41f, 2.15f, 16.92f)
                lineTo(5.83f, 14.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEA4335))) {
                moveTo(12.0f, 5.38f)
                curveTo(13.62f, 5.38f, 15.06f, 5.94f, 16.21f, 7.02f)
                lineTo(19.39f, 3.84f)
                curveTo(17.45f, 2.03f, 14.97f, 0.95f, 12.0f, 0.95f)
                curveTo(7.7f, 0.95f, 3.97f, 3.42f, 2.15f, 7.08f)
                lineTo(5.83f, 9.93f)
                curveTo(6.7f, 7.33f, 9.13f, 5.38f, 12.0f, 5.38f)
                close()
            }
        }.build()
    }
    Icon(imageVector = vector, contentDescription = "Google", modifier = modifier, tint = Color.Unspecified)
}
