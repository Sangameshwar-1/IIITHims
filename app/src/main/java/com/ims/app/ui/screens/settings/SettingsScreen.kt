package com.ims.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ims.app.ui.theme.*

/**
 * Settings screen providing app configuration and user profile management.
 * Includes language, timezone, grading system, and general preferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedTimezone by remember { mutableStateOf("IST (UTC+5:30)") }
    var selectedCurrency by remember { mutableStateOf("INR (₹)") }
    var autoGenerateIds by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showGradingDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── Profile Header ─────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.linearGradient(GradientPrimary))
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "A",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Administrator",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "admin@institute.edu",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                "Role: Super Admin",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        // ─── General Settings ───────────────────────────────
        item {
            Text(
                "General",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = "Language",
                    subtitle = selectedLanguage,
                    iconColor = Primary
                )
                HorizontalDivider(color = Divider)
                SettingsItem(
                    icon = Icons.Filled.AccessTime,
                    title = "Time Zone",
                    subtitle = selectedTimezone,
                    iconColor = Secondary
                )
                HorizontalDivider(color = Divider)
                SettingsItem(
                    icon = Icons.Filled.AttachMoney,
                    title = "Currency",
                    subtitle = selectedCurrency,
                    iconColor = Tertiary
                )
                HorizontalDivider(color = Divider)
                SettingsItem(
                    icon = Icons.Filled.LocationOn,
                    title = "Country",
                    subtitle = "India",
                    iconColor = Warning
                )
            }
        }

        // ─── Academic Settings ──────────────────────────────
        item {
            Text(
                "Academic",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Grade,
                    title = "Grading System",
                    subtitle = "GPA (10-point scale)",
                    iconColor = Primary,
                    onClick = { showGradingDialog = true }
                )
                HorizontalDivider(color = Divider)
                SettingsToggleItem(
                    icon = Icons.Filled.Tag,
                    title = "Auto-Generate Student IDs",
                    subtitle = "Format: STU-YEAR-XXXX",
                    isChecked = autoGenerateIds,
                    onCheckedChange = { autoGenerateIds = it },
                    iconColor = Secondary
                )
            }
        }

        // ─── App Preferences ────────────────────────────────
        item {
            Text(
                "Preferences",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            SettingsCard {
                SettingsToggleItem(
                    icon = Icons.Filled.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Always on",
                    isChecked = darkMode,
                    onCheckedChange = { darkMode = it },
                    iconColor = Primary
                )
                HorizontalDivider(color = Divider)
                SettingsToggleItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notifications",
                    subtitle = "Push and in-app alerts",
                    isChecked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    iconColor = Tertiary
                )
            }
        }

        // ─── About ──────────────────────────────────────────
        item {
            Text(
                "About",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            SettingsCard {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "App Version",
                    subtitle = "1.0.0 (Build 1)",
                    iconColor = Info
                )
                HorizontalDivider(color = Divider)
                SettingsItem(
                    icon = Icons.Filled.Code,
                    title = "Developer",
                    subtitle = "IMS Development Team",
                    iconColor = OnSurfaceVariant
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Logout action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error.copy(alpha = 0.12f),
                    contentColor = Error
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Grading System Dialog
    if (showGradingDialog) {
        AlertDialog(
            onDismissRequest = { showGradingDialog = false },
            title = { Text("Grading System", color = OnSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("GPA (10-point scale)", "GPA (4-point scale)", "Percentage", "CCE", "CWA").forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showGradingDialog = false }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = option == "GPA (10-point scale)", onClick = { showGradingDialog = false })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGradingDialog = false }) {
                    Text("Done", color = Primary)
                }
            },
            containerColor = SurfaceElevated,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(4.dp), content = content)
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = Primary,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = OnSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = OnSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconColor: Color = Primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = OnSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnPrimary,
                checkedTrackColor = Primary,
                uncheckedThumbColor = OnSurfaceVariant,
                uncheckedTrackColor = SurfaceElevated
            )
        )
    }
}
