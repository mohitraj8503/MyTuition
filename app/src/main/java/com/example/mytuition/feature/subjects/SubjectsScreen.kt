package com.example.mytuition.feature.subjects

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionMotion
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.Subject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SubjectsScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: SubjectsViewModel = viewModel(
        factory = SubjectsViewModel.provideFactory(AppContainer.getSubjectsUseCase)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.WarmIvory)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(
                text = "Subjects",
                style = MyTuitionTypography.Display,
                color = MyTuitionColors.DeepNavyText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your classes and materials.",
                style = MyTuitionTypography.Body,
                color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
            )
        }
        
        when (val uiState = state) {
            is SubjectsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PremiumPurple)
                }
            }
            is SubjectsUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.message,
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f),
                        style = MyTuitionTypography.Body
                    )
                }
            }
            is SubjectsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.message,
                        color = MyTuitionColors.PremiumCoral,
                        style = MyTuitionTypography.Body
                    )
                }
            }
            is SubjectsUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = MyTuitionSpacing.BottomNavPadding),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.subjects) { subject ->
                        SubjectClayCard(
                            subject = subject,
                            onClick = { onNavigateToDetail(subject.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectClayCard(
    subject: Subject,
    onClick: () -> Unit
) {
    val isPrimary = subject.name == "Mathematics"
    
    val bgColor = when {
        isPrimary -> MyTuitionColors.PremiumLime
        subject.name.contains("Physics", ignoreCase = true) -> MyTuitionColors.PremiumCoral
        subject.name.contains("Chemistry", ignoreCase = true) -> MyTuitionColors.PremiumBlue
        subject.name.contains("English", ignoreCase = true) -> MyTuitionColors.PremiumLavender
        else -> Color.White
    }
    
    val textColor = if (bgColor == MyTuitionColors.PremiumCoral) Color.White else MyTuitionColors.DeepNavyText
    val height = if (isPrimary) 220.dp else 160.dp

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = MyTuitionMotion.BouncySpring
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (isPrimary) 16.dp else 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = bgColor.copy(alpha = 0.1f),
                spotColor = bgColor.copy(alpha = 0.3f)
            )
            .background(bgColor, RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = subject.name,
                    style = MyTuitionTypography.LargeTitle.copy(
                        fontSize = if (isPrimary) 22.sp else 18.sp,
                        lineHeight = 26.sp
                    ),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subject.teacherName,
                    style = MyTuitionTypography.Metadata.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            
            Column {
                if (subject.homeworkCount > 0) {
                    Text(
                        text = "${subject.homeworkCount} HW pending",
                        style = MyTuitionTypography.Caption,
                        color = textColor
                    )
                }
                if (subject.nextClass != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val timeStr = dateFormat.format(Date(subject.nextClass))
                    Text(
                        text = "Next: $timeStr",
                        style = MyTuitionTypography.Metadata,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
