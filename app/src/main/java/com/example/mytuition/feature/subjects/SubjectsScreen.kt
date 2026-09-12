package com.example.mytuition.feature.subjects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.SubjectCard
import com.example.mytuition.core.di.AppContainer

@Composable
fun SubjectsScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: SubjectsViewModel = viewModel(
        factory = SubjectsViewModel.provideFactory(AppContainer.getSubjectsUseCase)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = MyTuitionSpacing.md)
            ) {
                Text(
                    text = "Subjects",
                    style = MyTuitionTypography.HeadlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                val countText = when (val uiState = state) {
                    is SubjectsUiState.Success -> "${uiState.subjects.size} enrolled courses"
                    else -> "Enrolled courses"
                }
                Text(
                    text = countText,
                    style = MyTuitionTypography.BodyMedium.copy(
                        fontSize = 15.sp,
                        color = MyTuitionColors.TextSecondary
                    )
                )
            }

            when (val uiState = state) {
                is SubjectsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = MyTuitionColors.PrimaryPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                is SubjectsUiState.Empty -> {
                    com.example.mytuition.core.designsystem.ClayZeroState(
                        title = "No Subjects Yet 📚",
                        subtitle = "Your enrolled subjects will appear here.",
                        modifier = Modifier.padding(vertical = 40.dp)
                    )
                }
                is SubjectsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.message,
                            color = MyTuitionColors.StatusRed
                        )
                    }
                }
                is SubjectsUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = MyTuitionSpacing.lg,
                            end = MyTuitionSpacing.lg,
                            bottom = 110.dp // clear floating nav bar
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.subjects,
                            key = { it.id }
                        ) { subject ->
                            val (iconColor, icon) = when {
                                subject.name.contains("Physic", ignoreCase = true) ->
                                    Pair(MyTuitionColors.SubjectPhysics, Icons.Rounded.Science)
                                subject.name.contains("Math", ignoreCase = true) ->
                                    Pair(MyTuitionColors.SubjectMath, Icons.Rounded.Calculate)
                                subject.name.contains("Chem", ignoreCase = true) ->
                                    Pair(MyTuitionColors.SubjectChemistry, Icons.Rounded.Biotech)
                                subject.name.contains("Bio", ignoreCase = true) ->
                                    Pair(MyTuitionColors.SubjectBio, Icons.Rounded.Spa)
                                subject.name.contains("Hist", ignoreCase = true) ->
                                    Pair(MyTuitionColors.SubjectHistory, Icons.Rounded.Schedule)
                                subject.name.contains("Geo", ignoreCase = true) ->
                                    Pair(MyTuitionColors.SubjectGeometry, Icons.Rounded.Public)
                                else ->
                                    Pair(MyTuitionColors.PrimaryPurple, Icons.Rounded.AutoStories)
                            }

                            SubjectCard(
                                subjectName = subject.name,
                                teacherName = subject.teacherName,
                                pendingTasks = subject.homeworkCount,
                                iconColor = iconColor,
                                icon = icon,
                                onClick = { onNavigateToDetail(subject.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
