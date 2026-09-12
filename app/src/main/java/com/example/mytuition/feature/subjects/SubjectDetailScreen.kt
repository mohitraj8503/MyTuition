package com.example.mytuition.feature.subjects

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.ClayZeroState
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.HomeworkItemCard
import com.example.mytuition.core.designsystem.components.LessonRow
import com.example.mytuition.core.designsystem.darken
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.model.Resource
import com.example.mytuition.core.domain.model.SubjectDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHomeworkDetail: (String) -> Unit,
    viewModel: SubjectDetailViewModel = viewModel(
        factory = SubjectDetailViewModel.provideFactory(subjectId, AppContainer.getSubjectDetailUseCase)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        when (val uiState = state) {
            is SubjectDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            is SubjectDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = MyTuitionColors.StatusRed)
                }
            }
            is SubjectDetailUiState.NotFound -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Subject not found", color = MyTuitionColors.TextSecondary)
                }
            }
            is SubjectDetailUiState.Success -> {
                SubjectDetailContent(
                    detail = uiState.detail,
                    onNavigateBack = onNavigateBack,
                    onNavigateToHomeworkDetail = onNavigateToHomeworkDetail
                )
            }
        }
    }
}

@Composable
private fun SubjectDetailContent(
    detail: SubjectDetail,
    onNavigateBack: () -> Unit,
    onNavigateToHomeworkDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lessons", "Resources", "Homework")

    Column(modifier = modifier.fillMaxSize()) {
        // Gradient purple header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MyTuitionColors.PrimaryPurpleDark,
                            MyTuitionColors.PrimaryPurple
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = MyTuitionSpacing.lg, vertical = MyTuitionSpacing.md)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Circular clay back button (48dp)
                val backSource = remember { MutableInteractionSource() }
                val isBackPressed by backSource.collectIsPressedAsState()
                val backScale by animateFloatAsState(
                    targetValue = if (isBackPressed) 0.94f else 1f,
                    animationSpec = MyTuitionAnimations.claySpring,
                    label = "backScale"
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(backScale)
                        .shadow(
                            elevation = if (isBackPressed) 2.dp else 6.dp,
                            shape = CircleShape,
                            spotColor = Color(0x30000000)
                        )
                        .clip(CircleShape)
                        .background(MyTuitionColors.CardWhite)
                        .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), CircleShape)
                        .clickable(
                            interactionSource = backSource,
                            indication = null,
                            onClick = onNavigateBack
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subject name
                Text(
                    text = detail.subject.name,
                    style = MyTuitionTypography.HeadlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Teacher name
                Text(
                    text = "By ${detail.subject.teacherName}",
                    style = MyTuitionTypography.BodyLarge.copy(
                        fontSize = 17.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Progress Bar (10dp tall)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "68% Complete • 18/26 Lessons",
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Clay Tab row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MyTuitionSpacing.lg)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tabs.forEachIndexed { index, tabName ->
                val isSelected = index == selectedTab
                val tabSource = remember { MutableInteractionSource() }
                val isTabPressed by tabSource.collectIsPressedAsState()

                val tabScale by animateFloatAsState(
                    targetValue = if (isTabPressed) 0.94f else if (isSelected) 1.02f else 1f,
                    animationSpec = MyTuitionAnimations.bounceSpring,
                    label = "tabScale"
                )
                val tabElevation by animateDpAsState(
                    targetValue = if (isTabPressed) 2.dp else if (isSelected) 6.dp else 3.dp,
                    animationSpec = MyTuitionAnimations.claySpringDp,
                    label = "tabElevation"
                )

                val bgColor = if (isSelected) MyTuitionColors.PrimaryPurple else MyTuitionColors.CardWhite
                val borderColor = if (isSelected) MyTuitionColors.PrimaryPurpleDark else MyTuitionColors.CardWhite.darken(0.08f)
                val textColor = if (isSelected) MyTuitionColors.TextOnPurple else MyTuitionColors.TextSecondary

                Box(
                    modifier = Modifier
                        .scale(tabScale)
                        .shadow(
                            elevation = tabElevation,
                            shape = MyTuitionShapes.PillShape,
                            spotColor = if (isSelected) MyTuitionColors.PrimaryPurple.copy(alpha = 0.35f) else Color(0x18000000)
                        )
                        .clip(MyTuitionShapes.PillShape)
                        .background(bgColor, MyTuitionShapes.PillShape)
                        .border(2.dp, borderColor, MyTuitionShapes.PillShape)
                        .clickable(
                            interactionSource = tabSource,
                            indication = null,
                            onClick = { selectedTab = index }
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabName,
                        style = MyTuitionTypography.LabelLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MyTuitionSpacing.lg)
        ) {
            when (selectedTab) {
                0 -> LessonsTabContent(detail = detail)
                1 -> ResourcesTabContent(resources = detail.recentResources)
                2 -> HomeworkTabContent(
                    homeworkList = detail.recentHomework,
                    onNavigateToHomeworkDetail = onNavigateToHomeworkDetail
                )
            }
        }
    }
}

@Composable
private fun LessonsTabContent(
    detail: SubjectDetail,
    modifier: Modifier = Modifier
) {
    val sampleLessons = listOf(
        Triple("Fundamentals & Overview", "45 min", Icons.Rounded.PlayCircle),
        Triple("Core Formulas and Application", "50 min", Icons.Rounded.Description),
        Triple("Problem Solving Workshop", "60 min", Icons.Rounded.TrackChanges),
        Triple("Revision & Advanced Q&A", "40 min", Icons.Rounded.PlayCircle)
    )

    val cardShape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = Color(0x221A1A1A),
                spotColor = Color(0x181A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        LazyColumn {
            items(sampleLessons) { (title, duration, resourceIcon) ->
                LessonRow(
                    icon = Icons.Rounded.MenuBook,
                    iconColor = MyTuitionColors.PrimaryPurple,
                    subjectName = title,
                    duration = duration,
                    resourceType = "Module",
                    resourceIcon = resourceIcon,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun ResourcesTabContent(
    resources: List<Resource>,
    modifier: Modifier = Modifier
) {
    if (resources.isEmpty()) {
        ClayZeroState(
            title = "No Resources Yet 📁",
            subtitle = "Study materials and downloads will appear here.",
            modifier = modifier.fillMaxSize()
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(resources) { resource ->
                val resShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = resShape,
                            ambientColor = Color(0x1A1A1A1A),
                            spotColor = Color(0x141A1A1A)
                        )
                        .clip(resShape)
                        .background(MyTuitionColors.CardWhite)
                        .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), resShape)
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.25f))
                                .clip(RoundedCornerShape(16.dp))
                                .background(MyTuitionColors.PrimaryPurpleLight)
                                .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Description,
                                contentDescription = null,
                                tint = MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = resource.title,
                                style = MyTuitionTypography.TitleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MyTuitionColors.TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            if (resource.sizeBytes != null) {
                                Text(
                                    text = "${resource.sizeBytes / 1024} KB • Download",
                                    style = MyTuitionTypography.LabelMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MyTuitionColors.PrimaryPurple
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = "Download",
                            tint = MyTuitionColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeworkTabContent(
    homeworkList: List<Homework>,
    onNavigateToHomeworkDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (homeworkList.isEmpty()) {
        ClayZeroState(
            title = "No Homework Pending 🎉",
            subtitle = "You are all caught up for this subject!",
            modifier = modifier.fillMaxSize()
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(homeworkList) { homework ->
                val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                val dueText = homework.dueAt?.let { "Due ${sdf.format(Date(it))}" } ?: "No deadline"

                HomeworkItemCard(
                    subjectTag = homework.subjectName,
                    subjectColor = MyTuitionColors.PrimaryPurple,
                    title = homework.title,
                    dueText = dueText,
                    isOverdue = homework.status == HomeworkStatus.OVERDUE,
                    isCompleted = homework.status == HomeworkStatus.COMPLETED,
                    hasAttachment = homework.attachments.isNotEmpty(),
                    onToggleComplete = {},
                    onClick = { onNavigateToHomeworkDetail(homework.id) }
                )
            }
        }
    }
}
