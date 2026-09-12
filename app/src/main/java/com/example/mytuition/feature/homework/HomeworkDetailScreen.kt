package com.example.mytuition.feature.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.BackHeader
import com.example.mytuition.core.designsystem.components.PillButton
import com.example.mytuition.core.designsystem.components.PillButtonVariant
import com.example.mytuition.core.designsystem.darken
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.model.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeworkDetailScreen(
    homeworkId: String,
    onNavigateBack: () -> Unit,
    viewModel: HomeworkDetailViewModel = viewModel(
        factory = HomeworkDetailViewModel.provideFactory(
            homeworkId,
            AppContainer.getHomeworkDetailUseCase,
            AppContainer.markHomeworkCompleteUseCase
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            BackHeader(
                title = "Homework Details",
                onBackClick = onNavigateBack
            )

            when (val uiState = state) {
                is HomeworkDetailUiState.Loading -> {
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
                }
                is HomeworkDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.message,
                            color = MyTuitionColors.StatusRed
                        )
                    }
                }
                is HomeworkDetailUiState.Success -> {
                    HomeworkDetailContent(
                        homework = uiState.homework,
                        modifier = Modifier.weight(1f),
                        onMarkComplete = viewModel::markComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeworkDetailContent(
    homework: Homework,
    modifier: Modifier = Modifier,
    onMarkComplete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    val subjectColor = when {
        homework.subjectName.contains("Math", ignoreCase = true) -> MyTuitionColors.SubjectMath
        homework.subjectName.contains("Physic", ignoreCase = true) -> MyTuitionColors.SubjectPhysics
        homework.subjectName.contains("Chem", ignoreCase = true) -> MyTuitionColors.SubjectChemistry
        homework.subjectName.contains("Hist", ignoreCase = true) -> MyTuitionColors.SubjectHistory
        homework.subjectName.contains("Geo", ignoreCase = true) -> MyTuitionColors.SubjectGeometry
        else -> MyTuitionColors.SubjectBio
    }

    val cardShape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MyTuitionSpacing.lg)
            .padding(bottom = MyTuitionSpacing.xxxl)
    ) {
        // Detail clay card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = cardShape,
                    ambientColor = Color(0x221A1A1A),
                    spotColor = Color(0x181A1A1A)
                )
                .clip(cardShape)
                .background(MyTuitionColors.CardWhite)
                .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
                .padding(22.dp)
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

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = subjectColor.copy(alpha = 0.2f))
                            .clip(RoundedCornerShape(12.dp))
                            .background(subjectColor.copy(alpha = 0.18f))
                            .border(1.5.dp, subjectColor.darken(0.1f).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = homework.subjectName,
                            style = MyTuitionTypography.LabelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = subjectColor
                            )
                        )
                    }

                    // Status Badge
                    val statusBg = if (homework.status == HomeworkStatus.COMPLETED) Color(0xFFE8F5E9) else MyTuitionColors.PrimaryPurpleLight
                    val statusBorder = if (homework.status == HomeworkStatus.COMPLETED) Color(0xFFC8E6C9) else MyTuitionColors.PrimaryPurpleLight.darken(0.08f)
                    val statusColor = if (homework.status == HomeworkStatus.COMPLETED) Color(0xFF2E7D32) else MyTuitionColors.PrimaryPurple

                    Box(
                        modifier = Modifier
                            .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x10000000))
                            .clip(MyTuitionShapes.PillShape)
                            .background(statusBg)
                            .border(1.5.dp, statusBorder, MyTuitionShapes.PillShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = homework.status.name,
                            style = MyTuitionTypography.LabelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = homework.title,
                    style = MyTuitionTypography.HeadlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MyTuitionColors.TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Assigned by ${homework.teacherName}",
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 15.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )

                if (homework.dueAt != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Due: ${dateFormat.format(Date(homework.dueAt))}",
                        style = MyTuitionTypography.BodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = homework.description,
                    style = MyTuitionTypography.BodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }
        }

        if (homework.attachments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Attachments",
                style = MyTuitionTypography.TitleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyTuitionColors.TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(homework.attachments) { resource ->
                    ResourceAttachmentCard(resource = resource)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (homework.status != HomeworkStatus.COMPLETED) {
            PillButton(
                text = "Mark as Completed",
                onClick = onMarkComplete,
                variant = PillButtonVariant.Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Composable
private fun ResourceAttachmentCard(resource: Resource) {
    val cardShape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, cardShape, spotColor = Color(0x12000000))
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(1.5.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(MyTuitionColors.PrimaryPurpleLight)
                    .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    tint = MyTuitionColors.PrimaryPurple,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = resource.title,
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                if (resource.sizeBytes != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${resource.sizeBytes / 1024} KB",
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 12.sp,
                            color = MyTuitionColors.TextSecondary
                        )
                    )
                }
            }
        }
    }
}
