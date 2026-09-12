package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.darken
import kotlinx.coroutines.launch

/**
 * 3D Puffy Claymorphic Card Surface — GPU-Optimized for 60/90/120Hz
 * - Uses graphicsLayer scaleX/scaleY (zero recomposition on press)
 * - Animatable instance with spring physics
 * - Retains full double-shadow and inner thickness gradient
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    cardColor: Color = MyTuitionColors.CardWhite,
    cornerRadius: Dp = 32.dp,
    elevation: Dp = 12.dp,
    borderWidth: Dp = 2.dp,
    borderColor: Color = cardColor.darken(0.08f),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isClickable = onClick != null

    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isPressed, isClickable) {
        if (isClickable) {
            scaleAnim.animateTo(
                targetValue = if (isPressed) 0.97f else 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
            )
        }
    }

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .shadow(
                elevation = if (isPressed && isClickable) 4.dp else elevation,
                shape = shape,
                ambientColor = Color(0x331A1A1A),
                spotColor = Color(0x221A1A1A)
            )
            .clip(shape)
            .background(cardColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        content()

        // Inner bottom shadow for 3D thickness
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.05f))
                    )
                )
        )
    }
}
