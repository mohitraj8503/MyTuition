package com.example.mytuition.feature.classdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.designsystem.darken

@Composable
fun ClassDetailScreen(
    classId: String = "today",
    onBackClick: () -> Unit = {},
    onJoinClassClick: () -> Unit = {},
    onMessageProfessor: () -> Unit = {},
    viewModel: ClassDetailViewModel = viewModel(factory = ClassDetailViewModel.provideFactory(classId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PastelBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Back Header with title
            BackHeader(
                title = "Today's Class",
                onBackClick = onBackClick
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = MyTuitionSpacing.lg)
                        .padding(bottom = MyTuitionSpacing.xxxl),
                    verticalArrangement = Arrangement.spacedBy(MyTuitionSpacing.lg)
                ) {
                    // 1. Professor Card
                    uiState.professor?.let { professor ->
                        ProfessorCard(
                            professor = professor,
                            onMessageClick = onMessageProfessor
                        )
                    }

                    // 2. Class Info Hero Card
                    uiState.classInfo?.let { classInfo ->
                        ClassHeroCard(
                            classInfo = classInfo,
                            onJoinClick = onJoinClassClick
                        )
                    }

                    // 3. Last Lessons Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "Last Lessons",
                            actionText = "See all",
                            onActionClick = {}
                        )

                        Spacer(modifier = Modifier.height(MyTuitionSpacing.sm))

                        // Container for lessons
                        val lessonsShape = RoundedCornerShape(28.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 8.dp,
                                    shape = lessonsShape,
                                    ambientColor = Color(0x221A1A1A),
                                    spotColor = Color(0x181A1A1A)
                                )
                                .clip(lessonsShape)
                                .background(MyTuitionColors.CardWhite)
                                .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), lessonsShape)
                                .padding(horizontal = 18.dp, vertical = 6.dp)
                        ) {
                            Column {
                                uiState.lastLessons.forEachIndexed { index, lesson ->
                                    LessonRow(
                                        icon = lesson.icon,
                                        iconColor = lesson.iconColor,
                                        subjectName = lesson.subjectName,
                                        duration = lesson.duration,
                                        resourceType = lesson.resourceType,
                                        resourceIcon = lesson.resourceIcon,
                                        showDivider = index < uiState.lastLessons.lastIndex,
                                        onClick = {}
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessorCard(
    professor: Professor,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                spotColor = Color(0x1E1A1A1A),
                ambientColor = Color(0x141A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
    ) {
        // Inner bottom shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with 3dp purple border and online dot
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(3.dp, MyTuitionColors.PrimaryPurple, CircleShape)
                        .background(MyTuitionColors.PrimaryPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AF",
                        style = MyTuitionTypography.TitleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }

                // Green online dot
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .border(2.5.dp, Color.White, CircleShape)
                        .background(MyTuitionColors.OnlineGreen)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Professor Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = professor.name,
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Professor",
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 15.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            // Message button (48dp puffy white clay circle, 22dp purple icon)
            val msgSource = remember { MutableInteractionSource() }
            val isMsgPressed by msgSource.collectIsPressedAsState()
            val msgScale by animateFloatAsState(
                targetValue = if (isMsgPressed) 0.94f else 1f,
                animationSpec = MyTuitionAnimations.claySpring,
                label = "msgBtnScale"
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(msgScale)
                    .shadow(
                        elevation = if (isMsgPressed) 3.dp else 8.dp,
                        shape = CircleShape,
                        spotColor = Color(0x201A1A1A),
                        ambientColor = Color(0x151A1A1A)
                    )
                    .clip(CircleShape)
                    .background(MyTuitionColors.CardWhite)
                    .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), CircleShape)
                    .clickable(
                        interactionSource = msgSource,
                        indication = null,
                        onClick = onMessageClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChatBubbleOutline,
                    contentDescription = "Message",
                    tint = MyTuitionColors.PrimaryPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ClassHeroCard(
    classInfo: ClassInfo,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                ambientColor = Color(0x331A1A1A),
                spotColor = Color(0x221A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardLavender)
            .border(2.dp, MyTuitionColors.CardLavender.darken(0.08f), cardShape)
    ) {
        // Inner bottom shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.05f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Top row: Green badge on white clay chip
            Box(
                modifier = Modifier
                    .shadow(3.dp, MyTuitionShapes.PillShape, spotColor = Color(0x15000000))
                    .clip(MyTuitionShapes.PillShape)
                    .background(MyTuitionColors.CardWhite)
                    .border(1.5.dp, MyTuitionColors.CardWhite.darken(0.08f), MyTuitionShapes.PillShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = classInfo.dateTime,
                        style = MyTuitionTypography.LabelLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Text(
                text = classInfo.title,
                style = MyTuitionTypography.HeadlineSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MyTuitionColors.TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "${classInfo.duration} • ${classInfo.type}",
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyTuitionColors.PrimaryPurple
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Chalkboard / physics doodle style graphic
            ChalkboardDoodle(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // PillButtonPrimary "Join Class Now →"
            PillButton(
                text = "Join Class Now →",
                onClick = onJoinClick,
                variant = PillButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ChalkboardDoodle(
    modifier: Modifier = Modifier
) {
    // Elegant simulated chalkboard with subtle physics & math formulas
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E3A2F)) // chalkboard dark green
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "E = mc²",
                    color = Color(0xCCFFFFFF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive
                )
                Text(
                    text = "F = ma",
                    color = Color(0x99FFFFFF),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Cursive
                )
                Text(
                    text = "Δp·Δx ≥ ℏ/2",
                    color = Color(0xBBFFFFFF),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Cursive
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚛ Atom Model",
                    color = Color(0xEE80CBC4),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "λ = h / mv",
                    color = Color(0xAAFFFFFF),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Cursive
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "∮ B·dA = 0",
                    color = Color(0x88FFFFFF),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Cursive
                )
                Text(
                    text = "v = d/t",
                    color = Color(0xAAFFFFFF),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Cursive
                )
                Text(
                    text = "G = 6.674×10⁻¹¹",
                    color = Color(0x88FFFFFF),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Cursive
                )
            }
        }
    }
}
