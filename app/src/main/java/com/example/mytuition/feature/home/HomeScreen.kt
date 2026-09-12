package com.example.mytuition.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.designsystem.darken
import com.example.mytuition.core.di.AppContainer

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToClassDetail: (String) -> Unit = {},
    onNavigateToHomeworkDetail: (String) -> Unit = {},
    onNavigateToSubjectDetail: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(AppContainer.homeRepository, AppContainer.authRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showRoomDirections by remember { mutableStateOf(false) }

    PastelBackground(
        blobColors = listOf(
            MyTuitionColors.BlobMint,
            MyTuitionColors.BlobPeach,
            MyTuitionColors.BlobLightBlue
        ),
        blobPositions = listOf(
            Offset(0.1f, 0.05f),  // top-left mint
            Offset(0.85f, 0.15f), // top-right peach
            Offset(0.5f, 0.8f)    // bottom light blue
        )
    ) {
        when (val uiState = state) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MyTuitionColors.PrimaryPurple,
                        strokeWidth = 3.dp
                    )
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.message,
                            style = MyTuitionTypography.BodyLarge,
                            color = MyTuitionColors.StatusRed
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PillButton(
                            text = "Retry",
                            onClick = { /* trigger reload */ }
                        )
                    }
                }
            }
            is HomeUiState.Success -> {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 100.dp) // Generous bottom padding to clear floating nav
                ) {
                    // Header: Grid Icon (Left) + Profile Avatar with Purple Ring (Right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tuition Brand: Mascot Logo + Tuition Name (e.g. "Chanakya Classes")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MyTuitionLogo(
                                size = 46.dp,
                                showClayCard = true
                            )

                            Column {
                                Text(
                                    text = (uiState as? HomeUiState.Success)?.tuitionName ?: "Chanakya Classes",
                                    style = MyTuitionTypography.TitleLarge.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MyTuitionColors.TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(MyTuitionColors.StatusGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Tuition Center",
                                        style = MyTuitionTypography.LabelSmall.copy(
                                            fontSize = 12.sp,
                                            color = MyTuitionColors.TextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        // Profile Avatar (48.dp circle, purple ring 3.dp, shadow 6.dp)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(6.dp, CircleShape, spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.3f))
                                .border(3.dp, MyTuitionColors.PrimaryPurple, CircleShape)
                                .clip(CircleShape)
                                .background(MyTuitionColors.PrimaryPurpleLight)
                                .clickable { onLogout() },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80",
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Greeting Section
                    Text(
                        text = "Next Class",
                        style = MyTuitionTypography.HeadlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keep learning, keep growing! ✨",
                        style = MyTuitionTypography.BodyMedium.copy(
                            fontSize = 15.sp,
                            color = MyTuitionColors.TextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Offline "Next Class" Card (Spacious, clean, photo-free)
                    val currentNextClass = uiState.nextClass ?: NextClassInfo()
                    NextClassCard(
                        info = currentNextClass,
                        onClick = { onNavigateToClassDetail(currentNextClass.id) },
                        onViewRoomClick = { showRoomDirections = true }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Class Schedule Section Header
                    SectionHeader(
                        title = "Class Schedule",
                        trailingText = "Aug 2025 ⌵",
                        onTrailingClick = { }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal scroll of DateChips (10dp gap)
                    val dateScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(dateScrollState),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.weekDates.forEach { weekDay ->
                            DateChip(
                                dayAbbreviation = weekDay.dayAbbr,
                                dateNumber = weekDay.dayNumber,
                                isActive = (weekDay.date == uiState.data.selectedDate),
                                onClick = { viewModel.selectDate(weekDay.date) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Timeline Section Header
                    SectionHeader(
                        title = "Timeline",
                        trailingText = "•••",
                        onTrailingClick = { }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Timeline List with continuous 4dp vertical connector line
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Continuous 4dp line behind all rows running full height without breaks
                        Canvas(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(top = 22.dp, bottom = 22.dp)
                        ) {
                            val lineX = (58.dp + 8.dp + 8.dp).toPx() // center of 16dp dot lane
                            drawLine(
                                color = Color(0xFFD8D2F8),
                                start = Offset(lineX, 0f),
                                end = Offset(lineX, size.height),
                                strokeWidth = 4.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (uiState.timeline.isEmpty()) {
                                Text(
                                    text = "No classes scheduled for this day.",
                                    style = MyTuitionTypography.BodyMedium,
                                    color = MyTuitionColors.TextSecondary,
                                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                                )
                            } else {
                                uiState.timeline.forEach { session ->
                                    val (icon, color) = when {
                                        session.subjectName.contains("sketch", ignoreCase = true) ||
                                        session.subjectName.contains("art", ignoreCase = true) ||
                                        session.subjectName.contains("draw", ignoreCase = true) ||
                                        session.subjectName.contains("creative", ignoreCase = true) -> Pair(Icons.Rounded.Brush, Color(0xFF8E24AA))
                                        session.subjectName.contains("math", ignoreCase = true) -> Pair(Icons.Rounded.Calculate, MyTuitionColors.SubjectMath)
                                        session.subjectName.contains("chem", ignoreCase = true) || session.subjectName.contains("sci", ignoreCase = true) -> Pair(Icons.Rounded.Science, MyTuitionColors.SubjectChemistry)
                                        session.subjectName.contains("eng", ignoreCase = true) -> Pair(Icons.Rounded.MenuBook, MyTuitionColors.SubjectEnglish)
                                        session.subjectName.contains("phys", ignoreCase = true) -> Pair(Icons.Rounded.Science, MyTuitionColors.SubjectPhysics)
                                        else -> Pair(Icons.Rounded.Palette, MyTuitionColors.PrimaryPurple)
                                    }

                                    TimelineSessionCard(
                                        time = session.time,
                                        subjectName = session.subjectName,
                                        subTopic = session.topic,
                                        iconColor = color,
                                        icon = icon,
                                        onMenuClick = { },
                                        onClick = { onNavigateToSubjectDetail(session.sessionId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRoomDirections) {
            val currentNextClass = (state as? HomeUiState.Success)?.nextClass ?: NextClassInfo()
            RoomDirectionsBottomSheet(
                info = currentNextClass,
                onDismiss = { showRoomDirections = false }
            )
        }
    }
}

/**
 * High-fidelity 3D Art Supplies illustration for the Featured Card (matches reference)
 */
@Composable
fun ArtSuppliesIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(115.dp),
        contentAlignment = Alignment.Center
    ) {
        // High quality 3D art supplies image from Unsplash / 3D render style
        AsyncImage(
            model = "https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=300&q=80",
            contentDescription = "Art Supplies",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        // Palette badge overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(34.dp)
                .shadow(4.dp, CircleShape)
                .background(MyTuitionColors.PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🎨", fontSize = 18.sp)
        }
    }
}
