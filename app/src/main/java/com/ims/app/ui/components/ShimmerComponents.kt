package com.ims.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ims.app.ui.theme.SurfaceVariant
import com.ims.app.ui.theme.SurfaceElevated

/**
 * Animated shimmer effect for loading states.
 * Use in place of content while data is being fetched.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    cornerRadius: Dp = 16.dp
) {
    val shimmerColors = listOf(
        SurfaceVariant,
        SurfaceElevated,
        SurfaceVariant
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * Shimmer card placeholder — mimics a list item loading state.
 */
@Composable
fun ShimmerListItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar placeholder
        ShimmerBox(
            modifier = Modifier.size(44.dp),
            height = 44.dp,
            cornerRadius = 12.dp
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(height = 14.dp, cornerRadius = 7.dp, modifier = Modifier.fillMaxWidth(0.6f))
            ShimmerBox(height = 12.dp, cornerRadius = 6.dp, modifier = Modifier.fillMaxWidth(0.4f))
        }
    }
}

/**
 * Shimmer stat card placeholder for dashboard loading state.
 */
@Composable
fun ShimmerStatCard(modifier: Modifier = Modifier) {
    ShimmerBox(
        modifier = modifier,
        height = 100.dp,
        cornerRadius = 20.dp
    )
}
