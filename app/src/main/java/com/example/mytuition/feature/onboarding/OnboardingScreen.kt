package com.example.mytuition.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.components.MyTuitionLogo
import com.example.mytuition.core.designsystem.components.OnboardingSlide
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.provideFactory(onComplete = onNavigateToLogin)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    // High quality student photos
    val imageUrls = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1577896851231-70ef18881754?auto=format&fit=crop&w=600&q=80"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(MyTuitionColors.CardWhite)
        ) { page ->
            when (page) {
                0 -> {
                    OnboardingSlide(
                        illustrationUrl = imageUrls[0],
                        headlinePrefix = "Thrilled to Join ",
                        headlineHighlight = "Your Learning Journey!",
                        subHeadline = "Explore a new world of knowledge, sharpen your skills, and grow together.",
                        primaryButtonText = "Continue",
                        currentPage = 0,
                        totalPages = 3,
                        onPrimaryClick = {
                            coroutineScope.launch {
                                viewModel.onNextPage()
                            }
                        },
                        onSecondaryClick = {}
                    )
                }
                1 -> {
                    OnboardingSlide(
                        illustrationUrl = imageUrls[1],
                        headlinePrefix = "Track Your Classes & ",
                        headlineHighlight = "Homework Effortlessly",
                        subHeadline = "Never miss a class. Stay on top of your assignments with gentle reminders.",
                        primaryButtonText = "Continue",
                        currentPage = 1,
                        totalPages = 3,
                        onPrimaryClick = {
                            coroutineScope.launch {
                                viewModel.onNextPage()
                            }
                        },
                        onSecondaryClick = {
                            coroutineScope.launch {
                                viewModel.onPreviousPage()
                            }
                        }
                    )
                }
                2 -> {
                    OnboardingSlide(
                        illustrationUrl = imageUrls[2],
                        headlinePrefix = "Parents ",
                        headlineHighlight = "Stay in the Loop",
                        subHeadline = "Get instant updates on your child's progress, attendance, and fees.",
                        primaryButtonText = "Get Started",
                        currentPage = 2,
                        totalPages = 3,
                        onPrimaryClick = {
                            viewModel.completeOnboarding()
                        },
                        onSecondaryClick = {
                            coroutineScope.launch {
                                viewModel.onPreviousPage()
                            }
                        }
                    )
                }
            }
        }

        // Small watermark logo in top-left corner (40dp, 60% opacity) on all slides
        MyTuitionLogo(
            size = 40.dp,
            showClayCard = false,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 20.dp, start = 20.dp)
                .alpha(0.6f)
                .align(Alignment.TopStart)
        )
    }
}
