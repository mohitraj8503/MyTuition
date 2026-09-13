package com.example.mytuition.feature.teacher.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.components.BottomNavItem
import com.example.mytuition.core.designsystem.components.FloatingBottomNav
import com.example.mytuition.feature.teacher.calendar.TeacherCalendarScreen
import com.example.mytuition.feature.teacher.homework.TeacherHomeworkReviewScreen
import com.example.mytuition.feature.teacher.students.TeacherStudentsScreen

@Composable
fun TeacherMainScreen(
    onLogout: () -> Unit,
    onNavigateToAttendance: (String) -> Unit = {},
    onNavigateToStudentProfile: (String) -> Unit = {},
    onNavigateToAddStudent: (String?) -> Unit = {},
    onNavigateToAnnouncements: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToHomeworkCreate: (String) -> Unit = {},
    onNavigateToHomeworkReview: (String) -> Unit = {}
) {
    var activeIndex by remember { mutableIntStateOf(0) }

    val navItems = remember {
        listOf(
            BottomNavItem("Home", Icons.Rounded.Home),
            BottomNavItem("Students", Icons.Rounded.Groups),
            BottomNavItem("Tasks", Icons.Rounded.Assignment),
            BottomNavItem("Calendar", Icons.Rounded.CalendarMonth)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.ScreenBackground)
    ) {
        // Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeIndex) {
                0 -> TeacherHomeScreen(
                    onLogout = onLogout,
                    onNavigateToAttendance = onNavigateToAttendance,
                    onNavigateToHomeworkReview = { activeIndex = 2 },
                    onNavigateToHomework = { activeIndex = 2 },
                    onNavigateToAnnouncements = onNavigateToAnnouncements,
                    onNavigateToStudents = { activeIndex = 1 },
                    onNavigateToBatchDetail = { onNavigateToAttendance(it) },
                    onNavigateToCalendar = onNavigateToCalendar,
                    onNavigateToEarnings = onNavigateToEarnings
                )
                1 -> TeacherStudentsScreen(
                    batchId = "batch_10a_maths",
                    onBackClick = { activeIndex = 0 },
                    onNavigateToStudentProfile = onNavigateToStudentProfile,
                    onNavigateToAddStudent = { onNavigateToAddStudent(null) }
                )
                2 -> TeacherHomeworkReviewScreen(
                    homeworkId = "hw_demo_1",
                    onBackClick = { activeIndex = 0 }
                )
                3 -> TeacherCalendarScreen(
                    onBackClick = { activeIndex = 0 }
                )
            }
        }

        // Floating Bottom Navigation
        FloatingBottomNav(
            items = navItems,
            activeIndex = activeIndex,
            onItemSelect = { activeIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
