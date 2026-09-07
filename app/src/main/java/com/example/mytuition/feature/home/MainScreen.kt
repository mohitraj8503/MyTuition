package com.example.mytuition.feature.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mytuition.feature.calendar.CalendarScreen
import com.example.mytuition.feature.profile.ProfileScreen
import com.example.mytuition.feature.homework.HomeworkScreen
import com.example.mytuition.feature.subjects.SubjectsScreen

val WarmIvory = Color(0xFFFBF9F6)
val DockBg = Color(0xFF6B4EFF) // Large rounded purple capsule
val ActiveIconColor = Color(0xFF1A1A24) // Deep Navy for contrast on lime
val InactiveIconColor = Color.White.copy(alpha = 0.6f)
val ActiveBubbleColor = Color(0xFFD4FF26) // Premium Lime

enum class MainTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    HOMEWORK("Homework", Icons.AutoMirrored.Filled.List),
    SUBJECTS("Subjects", Icons.AutoMirrored.Filled.MenuBook),
    CALENDAR("Calendar", Icons.Default.DateRange),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToHomeworkDetail: (String) -> Unit,
    onNavigateToSubjectDetail: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    Scaffold(
        bottomBar = {
            MyTuitionFloatingNav(
                tabs = MainTab.entries,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = WarmIvory
    ) { padding ->
        // Swap content based on tab
        Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
            when (selectedTab) {
                MainTab.HOME -> HomeScreen(
                    onLogout = onLogout,
                    onNavigateToHomeworkDetail = onNavigateToHomeworkDetail,
                    onNavigateToSubjectDetail = onNavigateToSubjectDetail,
                    onNavigateToSubjectsTab = { selectedTab = MainTab.SUBJECTS },
                    onNavigateToHomeworkTab = { selectedTab = MainTab.HOMEWORK }
                )
                MainTab.HOMEWORK -> HomeworkScreen(onNavigateToDetail = onNavigateToHomeworkDetail)
                MainTab.SUBJECTS -> SubjectsScreen(onNavigateToDetail = onNavigateToSubjectDetail)
                MainTab.CALENDAR -> CalendarScreen()
                MainTab.PROFILE -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun MyTuitionFloatingNav(
    tabs: List<MainTab>,
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp), // Floating above bottom edge
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f) // ~90% width
                .height(72.dp) // 68-76dp height
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(40.dp),
                    spotColor = DockBg.copy(alpha = 0.4f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .background(DockBg, RoundedCornerShape(40.dp))
                .padding(horizontal = 16.dp), // 14-20dp internal padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                val interactionSource = remember { MutableInteractionSource() }

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                    label = "scale"
                )

                val bubbleColor by animateColorAsState(
                    targetValue = if (isSelected) ActiveBubbleColor else Color.Transparent,
                    label = "bubbleColor"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) ActiveIconColor else InactiveIconColor,
                    label = "iconColor"
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(scale)
                        .background(bubbleColor, CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1A1A24))
    }
}
