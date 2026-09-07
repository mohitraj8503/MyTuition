package com.example.mytuition.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.di.AppContainer

val PremiumLime = Color(0xFFD4FF26)
val PremiumPurple = Color(0xFF7B52FF) // Softer, cleaner purple
val PremiumCoral = Color(0xFFFF8080)
val PremiumBlue = Color(0xFF90D0FF)
val PremiumLavender = Color(0xFFD2B0FF)
val DeepNavyText = Color(0xFF13131A)
val IvoryBackground = Color(0xFFFBF9F6)

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToHomeworkDetail: (String) -> Unit = {},
    onNavigateToSubjectDetail: (String) -> Unit = {},
    onNavigateToSubjectsTab: () -> Unit = {},
    onNavigateToHomeworkTab: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(AppContainer.authRepository)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IvoryBackground)
    ) {
        when (val uiState = state) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PremiumPurple)
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = PremiumCoral)
                }
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 48.dp, bottom = 140.dp), // Generous padding for dock
                    verticalArrangement = Arrangement.spacedBy(32.dp) // Clean whitespace
                ) {
                    // GREETING
                    item {
                        PremiumHeader(uiState = uiState)
                    }

                    // HERO OBJECT
                    item {
                        PremiumHeroObject(
                            uiState = uiState,
                            onClick = { onNavigateToSubjectDetail("sub_math") }
                        )
                    }

                    // SUBJECTS CAROUSEL
                    item {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Subjects",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = DeepNavyText
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onNavigateToSubjectsTab() }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "See all",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = DeepNavyText.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = DeepNavyText.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            SubjectsCarousel(onSubjectClick = onNavigateToSubjectDetail)
                        }
                    }

                    // PRODUCTIVITY COMPOSITION (HOMEWORK)
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(
                                text = "Things to finish",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = DeepNavyText
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PremiumHomeworkObject(
                                uiState = uiState,
                                onHomeworkClick = onNavigateToHomeworkDetail,
                                onSeeAllClick = onNavigateToHomeworkTab
                            )
                        }
                    }

                    // FEES & UPDATES (Quiet Information)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PremiumFeeObject(uiState.outstandingFee)
                            uiState.recentMessages.firstOrNull()?.let { msg ->
                                PremiumUpdateObject(msg)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumHeader(uiState: HomeUiState.Success) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "Good morning,",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = DeepNavyText.copy(alpha = 0.5f)
            )
            Text(
                text = "${uiState.studentName}.",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    letterSpacing = (-1).sp
                ),
                color = DeepNavyText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = uiState.className,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = DeepNavyText.copy(alpha = 0.4f)
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FloatingControlButton(icon = Icons.Default.Search) {}
            Box {
                FloatingControlButton(icon = Icons.Default.Notifications) {}
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp)
                        .size(8.dp)
                        .background(PremiumCoral, CircleShape)
                )
            }
        }
    }
}

@Composable
fun FloatingControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.05f), ambientColor = Color.Transparent)
            .background(Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = DeepNavyText, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun PremiumHeroObject(uiState: HomeUiState.Success, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        val nextClass = uiState.todayClasses.firstOrNull() ?: "No classes today"
        
        // Single subtle lime accent peeking from the top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-12).dp)
                .size(48.dp)
                .background(PremiumLime, CircleShape)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = PremiumPurple.copy(alpha = 0.3f), ambientColor = PremiumPurple.copy(alpha = 0.1f))
                .background(PremiumPurple, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .clickable(onClick = onClick)
        ) {
            Column(
                modifier = Modifier.padding(28.dp)
            ) {
                Text(
                    text = "YOUR DAY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                val parts = nextClass.split(" — ")
                if (parts.size == 2) {
                    Text(
                        text = "Next class",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = parts[0],
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp,
                            fontSize = 40.sp
                        ),
                        color = PremiumLime
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = parts[1],
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Quadratic Equations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = nextClass,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectsCarousel(onSubjectClick: (String) -> Unit = {}) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SubjectCardObject(
                title = "Mathematics",
                teacher = "Rajesh Sir",
                tasks = "2 tasks",
                color = PremiumLime,
                textColor = DeepNavyText,
                rotation = 0f,
                icon = { MathCanvasIcon(DeepNavyText) },
                onClick = { onSubjectClick("sub_math") }
            )
        }
        item {
            SubjectCardObject(
                title = "Physics",
                teacher = "Amit Sir",
                tasks = "1 task",
                color = PremiumCoral,
                textColor = Color.White,
                rotation = 1f,
                icon = { PhysicsCanvasIcon(Color.White) },
                onClick = { onSubjectClick("sub_physics") }
            )
        }
        item {
            SubjectCardObject(
                title = "Chemistry",
                teacher = "Pooja Ma'am",
                tasks = "No tasks",
                color = PremiumBlue,
                textColor = DeepNavyText,
                rotation = -1f,
                icon = { ChemCanvasIcon(DeepNavyText) },
                onClick = { onSubjectClick("sub_chem") }
            )
        }
        item {
            SubjectCardObject(
                title = "English",
                teacher = "Sarah Ma'am",
                tasks = "Read Ch 4",
                color = PremiumLavender,
                textColor = DeepNavyText,
                rotation = 0f,
                icon = { EnglishCanvasIcon(DeepNavyText) },
                onClick = { onSubjectClick("sub_eng") }
            )
        }
    }
}

@Composable
fun SubjectCardObject(
    title: String,
    teacher: String,
    tasks: String,
    color: Color,
    textColor: Color,
    rotation: Float,
    icon: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
    )

    Box(
        modifier = Modifier
            .rotate(rotation)
            .scale(scale)
            .width(140.dp)
            .height(160.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = color.copy(alpha = 0.3f), ambientColor = color.copy(alpha = 0.1f))
            .background(color, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 16.sp),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = teacher,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tasks,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun MathCanvasIcon(color: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.2f)
            lineTo(size.width * 0.4f, size.height * 0.2f)
            lineTo(size.width * 0.6f, size.height * 0.8f)
            lineTo(size.width * 0.9f, size.height * 0.2f)
        }
        drawPath(path, color, style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PhysicsCanvasIcon(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        drawOval(color, style = Stroke(width = 3f), size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.4f), topLeft = Offset(0f, size.height * 0.3f))
        rotate(60f) {
            drawOval(color, style = Stroke(width = 3f), size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.4f), topLeft = Offset(0f, size.height * 0.3f))
        }
        rotate(120f) {
            drawOval(color, style = Stroke(width = 3f), size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.4f), topLeft = Offset(0f, size.height * 0.3f))
        }
        drawCircle(color, radius = 4f, center = center)
    }
}

@Composable
fun ChemCanvasIcon(color: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.4f, size.height * 0.1f)
            lineTo(size.width * 0.6f, size.height * 0.1f)
            lineTo(size.width * 0.6f, size.height * 0.4f)
            lineTo(size.width * 0.9f, size.height * 0.9f)
            lineTo(size.width * 0.1f, size.height * 0.9f)
            lineTo(size.width * 0.4f, size.height * 0.4f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color, radius = 2f, center = Offset(size.width * 0.5f, size.height * 0.7f))
    }
}

@Composable
fun EnglishCanvasIcon(color: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.2f)
            lineTo(size.width * 0.5f, size.height * 0.3f)
            lineTo(size.width * 0.9f, size.height * 0.2f)
            lineTo(size.width * 0.9f, size.height * 0.9f)
            lineTo(size.width * 0.5f, size.height * 0.8f)
            lineTo(size.width * 0.1f, size.height * 0.9f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color, start = Offset(size.width * 0.5f, size.height * 0.3f), end = Offset(size.width * 0.5f, size.height * 0.8f), strokeWidth = 3f)
    }
}

@Composable
fun PremiumHomeworkObject(
    uiState: HomeUiState.Success,
    onHomeworkClick: (String) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f), ambientColor = Color.Transparent)
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(IvoryBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 0.6f },
                        color = PremiumLime,
                        trackColor = Color.Black.copy(alpha = 0.05f),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${uiState.homeworkDueCount}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = DeepNavyText
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Tasks remaining",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = DeepNavyText
                    )
                    Text(
                        text = "Due by tomorrow · Tap to view all",
                        style = MaterialTheme.typography.labelMedium,
                        color = DeepNavyText.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DeepNavyText.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onHomeworkClick("hw_1") }
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quadratic Equations",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = DeepNavyText
                    )
                    Text(
                        text = "Math",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PremiumPurple
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onHomeworkClick("hw_2") }
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Electricity",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = DeepNavyText
                    )
                    Text(
                        text = "Physics",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PremiumCoral
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumFeeObject(feeString: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Tuition Fee",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = DeepNavyText
            )
            Text(
                text = feeString,
                style = MaterialTheme.typography.labelMedium,
                color = PremiumCoral
            )
        }
        Box(
            modifier = Modifier
                .background(PremiumCoral.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Pending", color = PremiumCoral, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun PremiumUpdateObject(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(PremiumPurple, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Physics",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = DeepNavyText
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = DeepNavyText.copy(alpha = 0.7f)
            )
        }
    }
}
