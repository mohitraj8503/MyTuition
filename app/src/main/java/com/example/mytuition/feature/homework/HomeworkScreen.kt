package com.example.mytuition.feature.homework

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
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
    val state by viewModel.uiState.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.WarmIvory)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(
                text = "Homework",
                style = MyTuitionTypography.Display,
                color = MyTuitionColors.DeepNavyText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stay on top of your work.",
                style = MyTuitionTypography.Body,
                color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
            )
        }
        
        FilterRow(
            currentFilter = currentFilter,
            onFilterSelected = viewModel::setFilter
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        when (val uiState = state) {
            is HomeworkUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PremiumPurple)
                }
            }
            is HomeworkUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f))
                }
            }
            is HomeworkUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = MyTuitionColors.PremiumCoral)
                }
            }
            is HomeworkUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = MyTuitionSpacing.BottomNavPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.homeworkList) { homework ->
                        HomeworkItem(
                            homework = homework,
                            onClick = { onNavigateToDetail(homework.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    currentFilter: HomeworkFilter,
    onFilterSelected: (HomeworkFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HomeworkFilter.values().forEach { filter ->
            val isSelected = currentFilter == filter
            val bgColor by animateColorAsState(targetValue = if (isSelected) MyTuitionColors.PremiumPurple.copy(alpha = 0.15f) else Color.Transparent)
            val textColor by animateColorAsState(targetValue = if (isSelected) MyTuitionColors.PremiumPurple else MyTuitionColors.DeepNavyText.copy(alpha = 0.6f))
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MyTuitionTypography.Body.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun HomeworkItem(
    homework: Homework,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.04f), ambientColor = Color.Transparent)
            .background(Color.White, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homework.subjectName,
                    style = MyTuitionTypography.Caption,
                    color = MyTuitionColors.PremiumPurple
                )
                
                StatusBadge(status = homework.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = homework.title,
                style = MyTuitionTypography.LargeTitle.copy(fontSize = 18.sp),
                color = MyTuitionColors.DeepNavyText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${homework.teacherName}",
                    style = MyTuitionTypography.Metadata,
                    color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
                )
                
                if (homework.dueAt != null) {
                    Text(
                        text = "Due: ${dateFormat.format(Date(homework.dueAt))}",
                        style = MyTuitionTypography.Metadata.copy(fontWeight = FontWeight.Bold),
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: HomeworkStatus) {
    val containerColor = when (status) {
        HomeworkStatus.PENDING -> MyTuitionColors.PremiumLime.copy(alpha = 0.2f)
        HomeworkStatus.COMPLETED -> MyTuitionColors.PremiumBlue.copy(alpha = 0.2f)
        HomeworkStatus.SUBMITTED -> MyTuitionColors.PremiumLavender.copy(alpha = 0.2f)
        HomeworkStatus.OVERDUE -> MyTuitionColors.PremiumCoral.copy(alpha = 0.2f)
    }
    
    val contentColor = when (status) {
        HomeworkStatus.PENDING -> MyTuitionColors.DeepNavyText
        HomeworkStatus.COMPLETED -> MyTuitionColors.DeepNavyText
        HomeworkStatus.SUBMITTED -> MyTuitionColors.DeepNavyText
        HomeworkStatus.OVERDUE -> MyTuitionColors.PremiumCoral
    }

    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.name,
            style = MyTuitionTypography.Caption,
            color = contentColor
        )
    }
}
