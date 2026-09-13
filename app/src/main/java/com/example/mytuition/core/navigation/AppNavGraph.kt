package com.example.mytuition.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mytuition.feature.auth.LoginScreen
import com.example.mytuition.feature.classdetail.ClassDetailScreen
import com.example.mytuition.feature.home.MainScreen
import com.example.mytuition.feature.homework.HomeworkDetailScreen
import com.example.mytuition.feature.onboarding.OnboardingScreen
import com.example.mytuition.feature.splash.SplashScreen
import com.example.mytuition.feature.subjects.SubjectDetailScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        com.example.mytuition.core.data.network.SessionEvents.sessionExpired.collect {
            navController.navigate(Routes.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onNavigateToHome = { role ->
                    val destination = if (role == com.example.mytuition.core.domain.model.UserRole.TEACHER || role == com.example.mytuition.core.domain.model.UserRole.ADMIN) {
                        Routes.TeacherHome
                    } else {
                        Routes.Home
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Routes.Onboarding) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Onboarding) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role == com.example.mytuition.core.domain.model.UserRole.TEACHER || role == com.example.mytuition.core.domain.model.UserRole.ADMIN) {
                        Routes.TeacherHome
                    } else {
                        Routes.Home
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.Home) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
                onNavigateToClassDetail = { classId ->
                    navController.navigate(Routes.classDetailRoute(classId))
                },
                onNavigateToHomeworkDetail = { homeworkId ->
                    navController.navigate(Routes.homeworkDetailRoute(homeworkId))
                },
                onNavigateToSubjectDetail = { subjectId ->
                    navController.navigate(Routes.subjectDetailRoute(subjectId))
                }
            )
        }

        composable(
            route = "${Routes.ClassDetail}/{classId}",
            arguments = listOf(navArgument("classId") { 
                type = NavType.StringType 
                defaultValue = "today"
            })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: "today"
            ClassDetailScreen(
                classId = classId,
                onBackClick = { navController.popBackStack() },
                onJoinClassClick = { /* join live session */ },
                onMessageProfessor = { /* open chat */ }
            )
        }
        
        composable(
            route = "${Routes.HomeworkDetail}/{homeworkId}",
            arguments = listOf(navArgument("homeworkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val homeworkId = backStackEntry.arguments?.getString("homeworkId") ?: ""
            HomeworkDetailScreen(
                homeworkId = homeworkId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.SubjectDetail}/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            SubjectDetailScreen(
                subjectId = subjectId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHomeworkDetail = { homeworkId ->
                    navController.navigate(Routes.homeworkDetailRoute(homeworkId))
                }
            )
        }

        // ==========================================
        // TEACHER EXPERIENCES & SCREENS
        // ==========================================
        composable(Routes.TeacherHome) {
            com.example.mytuition.feature.teacher.home.TeacherMainScreen(
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.TeacherHome) { inclusive = true }
                    }
                },
                onNavigateToAttendance = { sessionId ->
                    navController.navigate(Routes.teacherAttendanceRoute(sessionId))
                },
                onNavigateToStudentProfile = { studentId ->
                    navController.navigate(Routes.teacherStudentProfileRoute(studentId))
                },
                onNavigateToAddStudent = { batchId ->
                    navController.navigate(Routes.teacherRegisterStudentRoute(batchId))
                },
                onNavigateToAnnouncements = {
                    navController.navigate(Routes.TeacherAnnouncements)
                },
                onNavigateToCalendar = {
                    navController.navigate(Routes.TeacherCalendar)
                },
                onNavigateToEarnings = {
                    navController.navigate(Routes.TeacherEarnings)
                },
                onNavigateToHomeworkCreate = { batchId ->
                    navController.navigate(Routes.teacherHomeworkCreateRoute(batchId))
                },
                onNavigateToHomeworkReview = { homeworkId ->
                    navController.navigate(Routes.teacherHomeworkReviewRoute(homeworkId))
                }
            )
        }

        composable(
            route = "${Routes.TeacherAttendance}/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            com.example.mytuition.feature.teacher.attendance.TeacherAttendanceScreen(
                sessionId = sessionId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.TeacherStudents}/{batchId}",
            arguments = listOf(navArgument("batchId") {
                type = NavType.StringType
                defaultValue = "batch_10a_maths"
            })
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getString("batchId") ?: "batch_10a_maths"
            com.example.mytuition.feature.teacher.students.TeacherStudentsScreen(
                batchId = batchId,
                onBackClick = { navController.popBackStack() },
                onNavigateToStudentProfile = { studentId ->
                    navController.navigate(Routes.teacherStudentProfileRoute(studentId))
                },
                onNavigateToAddStudent = {
                    navController.navigate(Routes.teacherRegisterStudentRoute(batchId))
                }
            )
        }

        composable(
            route = "${Routes.TeacherStudentProfile}/{studentId}",
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            com.example.mytuition.feature.teacher.students.TeacherStudentProfileScreen(
                studentId = studentId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.TeacherRegisterStudent) {
            com.example.mytuition.feature.teacher.students.register.RegisterStudentScreen(
                batchId = null,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.TeacherRegisterStudent}/{batchId}",
            arguments = listOf(navArgument("batchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getString("batchId")
            com.example.mytuition.feature.teacher.students.register.RegisterStudentScreen(
                batchId = batchId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.TeacherHomeworkCreate}/{batchId}",
            arguments = listOf(navArgument("batchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getString("batchId") ?: "batch_10a_maths"
            com.example.mytuition.feature.teacher.homework.TeacherHomeworkCreateScreen(
                batchId = batchId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.TeacherHomeworkReview}/{homeworkId}",
            arguments = listOf(navArgument("homeworkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val homeworkId = backStackEntry.arguments?.getString("homeworkId") ?: "hw_demo_1"
            com.example.mytuition.feature.teacher.homework.TeacherHomeworkReviewScreen(
                homeworkId = homeworkId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.TeacherAnnouncements) {
            com.example.mytuition.feature.teacher.announcements.TeacherAnnouncementsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.TeacherCalendar) {
            com.example.mytuition.feature.teacher.calendar.TeacherCalendarScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.TeacherEarnings) {
            com.example.mytuition.feature.teacher.earnings.TeacherEarningsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
