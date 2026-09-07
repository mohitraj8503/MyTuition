package com.example.mytuition.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mytuition.feature.auth.LoginScreen
import com.example.mytuition.feature.home.MainScreen
import com.example.mytuition.feature.splash.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.Home) {
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
        
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
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
                onNavigateToHomeworkDetail = { homeworkId ->
                    navController.navigate(Routes.homeworkDetailRoute(homeworkId))
                },
                onNavigateToSubjectDetail = { subjectId ->
                    navController.navigate(Routes.subjectDetailRoute(subjectId))
                }
            )
        }
        
        composable(
            route = "${Routes.HomeworkDetail}/{homeworkId}",
            arguments = listOf(navArgument("homeworkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val homeworkId = backStackEntry.arguments?.getString("homeworkId") ?: ""
            com.example.mytuition.feature.homework.HomeworkDetailScreen(
                homeworkId = homeworkId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.SubjectDetail}/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            com.example.mytuition.feature.subjects.SubjectDetailScreen(
                subjectId = subjectId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHomeworkDetail = { homeworkId ->
                    navController.navigate(Routes.homeworkDetailRoute(homeworkId))
                }
            )
        }
    }
}
