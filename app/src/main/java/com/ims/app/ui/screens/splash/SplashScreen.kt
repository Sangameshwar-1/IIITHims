package com.ims.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Animated splash screen shown on app launch.
 * Transitions to the main app after a brief animation.
 */
@Composable
fun SplashScreen(onComplete: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    // Logo scale animation
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    // Text alpha animation
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "textAlpha"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2000)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(GradientPrimary)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(110.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "IMS",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White
                )
            }

            // App name + tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsAlpha(alpha)
            ) {
                Text(
                    "Institute Management\nSystem",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Streamlining education, one module at a time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Version tag at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Text(
                "v1.0.0  •  2024102017 & 2024102054",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

// Extension to apply alpha without creating a separate composable
private fun Modifier.graphicsAlpha(alpha: Float): Modifier =
    this.then(Modifier.graphicsLayer { this.alpha = alpha })
