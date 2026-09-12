package com.example.mytuition.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Official MyTuition Brand Logo (Green book/person icon).
 */
@Composable
fun MyTuitionLogo(
    size: Dp = 80.dp,
    modifier: Modifier = Modifier,
    showClayCard: Boolean = true
) {
    val cardShape = CircleShape

    if (showClayCard) {
        Box(
            modifier = modifier
                .size(size)
                .shadow(
                    elevation = 12.dp,
                    shape = cardShape,
                    ambientColor = Color(0x331A1A1A),
                    spotColor = Color(0x221A1A1A)
                )
                .clip(cardShape)
                .background(Color.White)
                .border(2.dp, Color(0xFFF0EBFF), cardShape)
                .padding(size * 0.12f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mytuition_logo),
                contentDescription = "MyTuition Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_mytuition_logo),
            contentDescription = "MyTuition Logo",
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
    }
}
