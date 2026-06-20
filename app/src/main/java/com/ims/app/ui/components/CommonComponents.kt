package com.ims.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.ui.theme.*

/**
 * Gradient stat card for dashboard metrics with animated value display.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = value,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Custom search bar with animated focus state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search students, exams, courses…",
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear",
                        tint = OnSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceVariant,
            unfocusedContainerColor = SurfaceVariant,
            focusedBorderColor = Primary,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Primary
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface)
    )
}

/**
 * Section header with optional action button.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface,
            fontWeight = FontWeight.SemiBold
        )
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary
                )
            }
        }
    }
}

/**
 * Empty state placeholder with icon and message.
 */
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Filled.Inbox,
    title: String = "No data available",
    subtitle: String = "Items will appear here once added",
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = OnSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant.copy(alpha = 0.6f)
        )
        if (action != null) {
            Spacer(modifier = Modifier.height(16.dp))
            action()
        }
    }
}

/**
 * Styled info chip (tag) for displaying status, category, etc.
 */
@Composable
fun StatusChip(
    text: String,
    color: Color = Primary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Reusable list item card for students, exams, etc.
 */
@Composable
fun ImsListItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    leadingIconColor: Color = Primary,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(leadingIconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = leadingIconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailingContent?.invoke()
        }
    }
}

/**
 * Floating action button with gradient background.
 */
@Composable
fun GradientFab(
    onClick: () -> Unit,
    icon: ImageVector = Icons.Filled.Add,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.shadow(12.dp, CircleShape),
        containerColor = Primary,
        contentColor = OnPrimary,
        shape = CircleShape
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Add",
            modifier = Modifier.size(26.dp)
        )
    }
}

/**
 * Form text field with consistent styling.
 */
@Composable
fun ImsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = OnSurfaceVariant) }
            } else null,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            readOnly = readOnly,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                errorBorderColor = Error,
                cursorColor = Primary
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface)
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
