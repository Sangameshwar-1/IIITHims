package com.ims.app.ui.screens.examination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ims.app.ui.components.*
import com.ims.app.ui.theme.*

/**
 * Analytics and reporting screen for examinations.
 * Shows summary stats, exam-wise performance, and simple visual bars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamReportsScreen(
    viewModel: ExaminationViewModel,
    onBack: () -> Unit
) {
    val allExams by viewModel.allExams.collectAsState()
    val totalCount by viewModel.totalExamCount.collectAsState()
    val completedExams = allExams.filter { it.status == "Completed" }
    val scheduledExams = allExams.filter { it.status == "Scheduled" }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Reports & Analytics", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = OnSurface,
                    navigationIconContentColor = OnSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Summary Cards ──────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Exams",
                        value = "$totalCount",
                        icon = Icons.Filled.Assignment,
                        gradient = CardGradient1,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Completed",
                        value = "${completedExams.size}",
                        icon = Icons.Filled.CheckCircle,
                        gradient = CardGradient2,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Scheduled",
                        value = "${scheduledExams.size}",
                        icon = Icons.Filled.Schedule,
                        gradient = CardGradient4,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pass Rate",
                        value = "—",
                        icon = Icons.Filled.TrendingUp,
                        gradient = CardGradient3,
                        modifier = Modifier.weight(1f),
                        subtitle = "Aggregate"
                    )
                }
            }

            // ─── Performance Chart ──────────────────────────
            item {
                SectionHeader(title = "Exam Performance Overview")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Marks Distribution",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Simulated bar chart using Compose
                        if (allExams.isNotEmpty()) {
                            allExams.take(6).forEach { exam ->
                                val maxMarks = exam.totalMarks.toFloat()
                                val fillFraction = if (maxMarks > 0) {
                                    (exam.passingMarks.toFloat() / maxMarks).coerceIn(0f, 1f)
                                } else 0.3f

                                val barColor = when (exam.status) {
                                    "Completed" -> Success
                                    "Scheduled" -> Info
                                    else -> Warning
                                }

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            exam.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurface,
                                            modifier = Modifier.weight(0.45f),
                                            maxLines = 1
                                        )
                                        Text(
                                            "${exam.totalMarks} marks",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(barColor.copy(alpha = 0.1f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fillFraction)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Brush.horizontalGradient(listOf(barColor, barColor.copy(alpha = 0.6f))))
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        } else {
                            Text(
                                "No exam data available for charts",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // ─── Exam-wise Breakdown ────────────────────────
            item {
                SectionHeader(title = "All Exams Breakdown")
            }

            if (allExams.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.BarChart,
                        title = "No exam data",
                        subtitle = "Create exams to see analytics here"
                    )
                }
            } else {
                items(allExams) { exam ->
                    val statusColor = when (exam.status) {
                        "Scheduled" -> Info
                        "Ongoing" -> Warning
                        "Completed" -> Success
                        "Cancelled" -> Error
                        else -> OnSurfaceVariant
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(statusColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Assignment,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    exam.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(exam.date, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.5f))
                                    Text("•", color = OnSurfaceVariant.copy(alpha = 0.3f))
                                    Text("${exam.totalMarks} marks", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.5f))
                                }
                            }

                            StatusChip(text = exam.status, color = statusColor)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
