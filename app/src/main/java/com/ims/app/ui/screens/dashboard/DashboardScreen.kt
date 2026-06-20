package com.ims.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.data.model.Exam
import com.ims.app.data.model.News
import com.ims.app.data.model.Student
import com.ims.app.ui.components.ImsListItem
import com.ims.app.ui.components.ImsSearchBar
import com.ims.app.ui.components.SectionHeader
import com.ims.app.ui.components.StatCard
import com.ims.app.ui.components.StatusChip
import com.ims.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ═══════════════════════════════════════════════════════════════════
//  Dashboard Screen – Premium dark-themed overview
// ═══════════════════════════════════════════════════════════════════

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToAdmissions: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToNewAdmission: () -> Unit,
    onNavigateToCreateExam: () -> Unit
) {
    // ── Collect all state ───────────────────────────────────────────
    val totalStudents by viewModel.totalStudents.collectAsState()
    val activeCourses by viewModel.activeCourses.collectAsState()
    val upcomingExamCount by viewModel.upcomingExamCount.collectAsState()
    val latestNews by viewModel.latestNews.collectAsState()
    val upcomingExams by viewModel.upcomingExams.collectAsState()
    val recentStudents by viewModel.recentStudents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val userRole by viewModel.currentUserRole.collectAsState()

    // ── Entrance visibility flag ────────────────────────────────────
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── 1. Greeting header ──────────────────────────────────────
        item(key = "greeting") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { -it / 2 }
                )
            ) {
                GreetingHeader(userRole = userRole)
            }
        }

        // ── 2. Search bar ───────────────────────────────────────────
        item(key = "search") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 100))
            ) {
                ImsSearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // ── 3. Stat cards (2 × 2 grid) ─────────────────────────────
        item(key = "stats") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 200))
            ) {
                StatCardGrid(
                    totalStudents = totalStudents,
                    activeCourses = activeCourses,
                    upcomingExamCount = upcomingExamCount,
                    onStudentsClick = onNavigateToAdmissions,
                    onExamsClick = onNavigateToExams
                )
            }
        }

        // ── 4. Quick Actions ────────────────────────────────────────
        item(key = "quick_actions") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 300))
            ) {
                QuickActionsRow(
                    onNewAdmission = onNavigateToNewAdmission,
                    onCreateExam = onNavigateToCreateExam,
                    onViewReports = onNavigateToExams,
                    onSettings = {}
                )
            }
        }

        // ── 5. Upcoming exams ───────────────────────────────────────
        item(key = "exams_header") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 400))
            ) {
                SectionHeader(
                    title = "Upcoming Exams",
                    actionText = "View All",
                    onActionClick = onNavigateToExams,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        if (upcomingExams.isEmpty()) {
            item(key = "exams_empty") {
                EmptySection(message = "No upcoming exams")
            }
        } else {
            items(upcomingExams.take(4), key = { "exam_${it.id}" }) { exam ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 450))
                ) {
                    ExamCard(exam = exam)
                }
            }
        }

        // ── 6. Latest news ──────────────────────────────────────────
        item(key = "news_header") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 500))
            ) {
                SectionHeader(
                    title = "Latest News",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        if (latestNews.isEmpty()) {
            item(key = "news_empty") {
                EmptySection(message = "No news available")
            }
        } else {
            items(latestNews, key = { "news_${it.id}" }) { news ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 550))
                ) {
                    NewsCard(news = news)
                }
            }
        }

        // ── 7. Recent students ──────────────────────────────────────
        item(key = "students_header") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 600))
            ) {
                SectionHeader(
                    title = "Recent Students",
                    actionText = "View All",
                    onActionClick = onNavigateToAdmissions,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        if (recentStudents.isEmpty()) {
            item(key = "students_empty") {
                EmptySection(message = "No students found")
            }
        } else {
            itemsIndexed(recentStudents, key = { _, s -> "student_${s.id}" }) { _, student ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 650))
                ) {
                    ImsListItem(
                        title = "${student.firstName} ${student.lastName}",
                        subtitle = "${student.studentId}  •  ${student.email}",
                        leadingIcon = Icons.Filled.Person,
                        leadingIconColor = Primary,
                        trailingContent = {
                            StatusChip(
                                text = student.status,
                                color = when (student.status) {
                                    "Active" -> Success
                                    "Graduated" -> Info
                                    else -> Warning
                                }
                            )
                        }
                    )
                }
            }
        }

        // Bottom spacer for system navigation bar
        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  PRIVATE COMPOSABLE SECTIONS
// ═══════════════════════════════════════════════════════════════════

/**
 * Top greeting bar with avatar circle, welcome text, and notification bell.
 */
@Composable
private fun GreetingHeader(userRole: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar circle with gradient
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(GradientPrimary)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userRole.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    text = userRole,
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // Notification bell
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { /* TODO: notifications */ }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            // Notification dot
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Tertiary)
            )
        }
    }
}

/**
 * 2 × 2 grid of gradient [StatCard]s displaying key dashboard metrics.
 */
@Composable
private fun StatCardGrid(
    totalStudents: Int,
    activeCourses: Int,
    upcomingExamCount: Int,
    onStudentsClick: () -> Unit,
    onExamsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessLow)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Students",
                value = totalStudents.toString(),
                icon = Icons.Filled.People,
                gradient = CardGradient1,
                modifier = Modifier.weight(1f),
                subtitle = "All enrolled",
                onClick = onStudentsClick
            )
            StatCard(
                title = "Active Courses",
                value = activeCourses.toString(),
                icon = Icons.Filled.MenuBook,
                gradient = CardGradient2,
                modifier = Modifier.weight(1f),
                subtitle = "Currently running"
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Upcoming Exams",
                value = upcomingExamCount.toString(),
                icon = Icons.Filled.Assignment,
                gradient = CardGradient3,
                modifier = Modifier.weight(1f),
                subtitle = "Scheduled",
                onClick = onExamsClick
            )
            StatCard(
                title = "Pending Admissions",
                value = "0",
                icon = Icons.Filled.PersonAdd,
                gradient = CardGradient4,
                modifier = Modifier.weight(1f),
                subtitle = "Awaiting review"
            )
        }
    }
}

/**
 * Horizontal row of 4 quick-action circular buttons.
 */
@Composable
private fun QuickActionsRow(
    onNewAdmission: () -> Unit,
    onCreateExam: () -> Unit,
    onViewReports: () -> Unit,
    onSettings: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Filled.PersonAdd,
                label = "New\nAdmission",
                gradient = GradientPrimary,
                onClick = onNewAdmission
            )
            QuickActionButton(
                icon = Icons.Filled.NoteAdd,
                label = "Create\nExam",
                gradient = GradientSecondary,
                onClick = onCreateExam
            )
            QuickActionButton(
                icon = Icons.Filled.Assessment,
                label = "View\nReports",
                gradient = GradientWarm,
                onClick = onViewReports
            )
            QuickActionButton(
                icon = Icons.Filled.Settings,
                label = "Settings",
                gradient = listOf(Color(0xFF64748B), Color(0xFF475569)),
                onClick = onSettings
            )
        }
    }
}

/**
 * Single circular quick-action button with gradient background.
 */
@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/**
 * Premium exam card showing date badge, name, venue, and time.
 */
@Composable
private fun ExamCard(exam: Exam) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessLow)),
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
            // Date badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(GradientPrimary)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatDay(exam.date),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = formatMonth(exam.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = OnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = exam.venue.ifBlank { "TBA" },
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Time chip
            Column(horizontalAlignment = Alignment.End) {
                StatusChip(text = exam.status, color = examStatusColor(exam.status))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exam.startTime} – ${exam.endTime}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Premium news card with title preview, category chip, and date.
 */
@Composable
private fun NewsCard(news: News) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessLow)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(
                    text = news.category,
                    color = newsCategoryColor(news.category)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.content,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = OnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = news.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
                Text(
                    text = formatTimestamp(news.publishedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

/**
 * Simple empty-state row shown when a section has no data.
 */
@Composable
private fun EmptySection(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  UTILITIES
// ═══════════════════════════════════════════════════════════════════

/** Extracts the day portion from an ISO date string (yyyy-MM-dd). */
private fun formatDay(isoDate: String): String = try {
    isoDate.split("-").getOrNull(2) ?: "--"
} catch (_: Exception) { "--" }

/** Extracts the abbreviated month from an ISO date string. */
private fun formatMonth(isoDate: String): String = try {
    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    val idx = (isoDate.split("-").getOrNull(1)?.toIntOrNull() ?: 1) - 1
    months.getOrElse(idx) { "---" }
} catch (_: Exception) { "---" }

/** Converts a millis timestamp to a readable date string. */
private fun formatTimestamp(millis: Long): String = try {
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
} catch (_: Exception) { "" }

/** Returns a semantic color for the exam status chip. */
private fun examStatusColor(status: String): Color = when (status) {
    "Scheduled" -> Info
    "Ongoing"   -> Warning
    "Completed" -> Success
    "Cancelled" -> Error
    else        -> OnSurfaceVariant
}

/** Returns a semantic color for the news category chip. */
private fun newsCategoryColor(category: String): Color = when (category) {
    "Academic" -> Primary
    "Event"    -> Secondary
    "Holiday"  -> Tertiary
    "General"  -> Info
    else       -> OnSurfaceVariant
}
