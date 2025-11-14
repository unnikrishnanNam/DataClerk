package com.unnikrishnan.dataclerk.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unnikrishnan.dataclerk.ui.components.PulsingLoadingIndicator
import com.unnikrishnan.dataclerk.ui.theme.AccentPrimary
import com.unnikrishnan.dataclerk.ui.theme.AccentSecondary
import com.unnikrishnan.dataclerk.ui.theme.TextPrimary
import com.unnikrishnan.dataclerk.ui.theme.TextSecondary

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToError: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val loadingText by viewModel.loadingText.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    
    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is SplashViewModel.NavigationEvent.NavigateToHome -> {
                onNavigateToHome()
                viewModel.clearNavigationEvent()
            }
            is SplashViewModel.NavigationEvent.NavigateToError -> {
                onNavigateToError()
                viewModel.clearNavigationEvent()
            }
            else -> {}
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo with animation
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "Data Clerk Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha),
                tint = AccentPrimary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Name
            Text(
                text = "Data Clerk",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Your Database Assistant",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Loading indicator
            PulsingLoadingIndicator()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Loading text
            Text(
                text = loadingText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.alpha(0.8f)
            )
        }
    }
}
