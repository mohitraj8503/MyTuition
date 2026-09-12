package com.example.mytuition.feature.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.darken
import com.example.mytuition.core.designsystem.components.FilterChipRow
import com.example.mytuition.core.designsystem.components.HomeworkItemCard
import com.example.mytuition.core.designsystem.components.ProgressRing
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeworkScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeworkViewModel = viewModel(
        factory = HomeworkViewModel.provideFactory(AppContainer.getHomeworkListUseCase)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val allHomeworkList by viewModel.allHomework.collectAsStateWithLifecycle()

    val totalCount = allHomeworkList.size
    val pendingCount = allHomeworkList.count { it.status == com.example.mytuition.core.domain.model.HomeworkStatus.PENDING }
    val completedCount = allHomeworkList.count { it.status == com.example.mytuition.core.domain.model.HomeworkStatus.COMPLETED }
    val overdueCount = allHomeworkList.count { it.status == com.example.mytuition.core.domain.model.HomeworkStatus.OVERDUE }

    val filterLabels = listOf(
        "All ($totalCount)",
        "Pending ($pendingCount)",
        "Completed ($completedCount)",
        "Overdue ($overdueCount)"
    )

    val activeIndex = when (currentFilter) {
        HomeworkFilter.ALL -> 0
        HomeworkFilter.PENDING -> 1
        HomeworkFilter.COMPLETED -> 2
        HomeworkFilter.OVERDUE -> 3
    }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: "Homework" (32sp Bold) + "Weekly Overview" (15sp Regular)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = MyTuitionSpacing.md)
            ) {
                Text(
                    text = "Homework",
                    style = MyTuitionTypography.HeadlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Weekly Overview",
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 15.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = MyTuitionSpacing.lg,
                    end = MyTuitionSpacing.lg,
                    bottom = 110.dp // clear floating nav bar
                ),
                verticalArrangement = Arrangement.spacedBy(MyTuitionSpacing.md)
            ) {
                // Overview Card
                item {
                    OverviewCard(
                        completed = completedCount,
                        total = totalCount,
                        pending = pendingCount,
                        overdue = overdueCount
                    )
                }

                // Filter Row
                item {
                    FilterChipRow(
                        filters = filterLabels,
                        activeIndex = activeIndex,
                        onFilterSelect = { idx ->
                            when (idx) {
                                0 -> viewModel.setFilter(HomeworkFilter.ALL)
                                1 -> viewModel.setFilter(HomeworkFilter.PENDING)
                                2 -> viewModel.setFilter(HomeworkFilter.COMPLETED)
                                3 -> viewModel.setFilter(HomeworkFilter.OVERDUE)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Homework list items
                when (val uiState = state) {
                    is HomeworkUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MyTuitionColors.PrimaryPurple,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                    is HomeworkUiState.Empty -> {
                        item {
                            com.example.mytuition.core.designsystem.ClayZeroState(
                                title = "All Clear! 🎉",
                                subtitle = uiState.message,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    }
                    is HomeworkUiState.Error -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.message,
                                    color = MyTuitionColors.StatusRed
                                )
                            }
                        }
                    }
                    is HomeworkUiState.Success -> {
                        items(
                            items = uiState.homeworkList,
                            key = { it.id }
                        ) { homework ->
                            val subjectColor = when {
                                homework.subjectName.contains("Math", ignoreCase = true) -> MyTuitionColors.SubjectMath
                                homework.subjectName.contains("Physic", ignoreCase = true) -> MyTuitionColors.SubjectPhysics
                                homework.subjectName.contains("Chem", ignoreCase = true) -> MyTuitionColors.SubjectChemistry
                                homework.subjectName.contains("Hist", ignoreCase = true) -> MyTuitionColors.SubjectHistory
                                homework.subjectName.contains("Geo", ignoreCase = true) -> MyTuitionColors.SubjectGeometry
                                else -> MyTuitionColors.SubjectBio
                            }

                            val isCompleted = homework.status == HomeworkStatus.COMPLETED
                            val isOverdue = homework.status == HomeworkStatus.OVERDUE

                            val dueText = if (homework.dueAt != null) {
                                val sdf = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
                                "Due ${sdf.format(Date(homework.dueAt))}"
                            } else {
                                "No due date"
                            }

                            HomeworkItemCard(
                                subjectTag = homework.subjectName,
                                subjectColor = subjectColor,
                                title = homework.title,
                                dueText = dueText,
                                isOverdue = isOverdue,
                                isCompleted = isCompleted,
                                hasAttachment = homework.attachments.isNotEmpty(),
                                onToggleComplete = {
                                    viewModel.toggleHomeworkComplete(homework.id)
                                },
                                onClick = { onNavigateToDetail(homework.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(
    completed: Int,
    total: Int,
    pending: Int,
    overdue: Int,
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
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
    ) {
        // Inner bottom shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: ProgressRing
            ProgressRing(
                completed = completed,
                total = total,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            // Right: Column of stats
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tasks this week",
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MyTuitionColors.TextSecondary
                    )
                )

                Text(
                    text = "$pending Pending",
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.PrimaryPurple
                    )
                )

                Text(
                    text = "$completed Completed",
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.OnlineGreen
                    )
                )

                if (overdue > 0) {
                    Text(
                        text = "$overdue Overdue",
                        style = MyTuitionTypography.TitleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.StatusRed
                        )
                    )
                }
            }
        }
    }
}
