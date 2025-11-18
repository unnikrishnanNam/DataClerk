package com.unnikrishnan.dataclerk.ui.screens.error

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unnikrishnan.dataclerk.data.preferences.PreferencesManager
import com.unnikrishnan.dataclerk.ui.components.PillButton
import com.unnikrishnan.dataclerk.ui.theme.*

@Composable
fun ErrorScreen(
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    var backendUrl by remember { mutableStateOf(prefsManager.baseUrl) }
    var showUrlInput by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    // Animation for the error icon
    val infiniteTransition = rememberInfiniteTransition(label = "error")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
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
            modifier = Modifier.padding(32.dp)
        ) {
            // Error Icon
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                color = ErrorColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Connection Error",
                        modifier = Modifier.size(60.dp),
                        tint = ErrorColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Error Title
            Text(
                text = "Connection Failed",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Error Message
            Text(
                text = "Unable to connect to the backend server.\nPlease check your connection and try again.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Backend URL Configuration
            if (showUrlInput) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = ShadowDark,
                            spotColor = ShadowDark
                        ),
                    color = SurfaceElevated1,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Backend URL",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = backendUrl,
                            onValueChange = { backendUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Enter backend URL")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentPrimary,
                                unfocusedBorderColor = BorderColor
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showUrlInput = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextSecondary
                                )
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    prefsManager.baseUrl = backendUrl
                                    // update retrofit client with new base URL
                                    try {
                                        com.unnikrishnan.dataclerk.data.api.RetrofitClient.updateBaseUrl(backendUrl)
                                    } catch (_: Exception) {}
                                    if (prefsManager.enableHaptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showUrlInput = false
                                    onRetry()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentPrimary
                                )
                            ) {
                                Text("Save & Retry")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Retry Button
            PillButton(
                text = "Retry Connection",
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                icon = Icons.Default.Refresh,
                backgroundColor = AccentPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Change Backend URL Button
            OutlinedButton(
                onClick = { showUrlInput = !showUrlInput },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AccentPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Backend URL")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Additional info
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = ShadowDark,
                        spotColor = ShadowDark
                    ),
                color = SurfaceElevated1,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Troubleshooting Tips:",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Check if the backend server is running",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "• Verify your network connection",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "• Ensure the API URL is correct in settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
