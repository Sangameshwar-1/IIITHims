package com.ims.app.ui.screens.examination

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ims.app.ui.components.*
import com.ims.app.ui.theme.*

/**
 * Lists all exams with search, status filtering, and navigation to details/creation.
 */
@Composable
fun ExamListScreen(
    viewModel: ExaminationViewModel,
    onExamClick: (Long) -> Unit,
    onCreateExam: () -> Unit,
    onReportsClick: () -> Unit
) {
    val exams by viewModel.filteredExams.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val totalCount by viewModel.totalExamCount.collectAsState()

    val filterOptions = listOf("All", "Scheduled", "Ongoing", "Completed", "Cancelled")

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            GradientFab(onClick = onCreateExam)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ─── Header ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Examinations",
                            style = MaterialTheme.typography.displaySmall,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$totalCount exams total",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(
                        onClick = onReportsClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary.copy(alpha = 0.12f))
                    ) {
                        Icon(Icons.Filled.BarChart, contentDescription = "Reports", tint = Primary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ─── Search ─────────────────────────────────────
            item {
                ImsSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search exams by name…"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ─── Filter Chips ───────────────────────────────
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { option ->
                        val selected = statusFilter == option
                        val chipColor = when (option) {
                            "Scheduled" -> Info
                            "Ongoing" -> Warning
                            "Completed" -> Success
                            "Cancelled" -> Error
                            else -> Primary
                        }
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.onStatusFilterChanged(option) },
                            label = {
                                Text(
                                    option,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = chipColor.copy(alpha = 0.15f),
                                selectedLabelColor = chipColor,
                                containerColor = SurfaceVariant,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = Border,
                                selectedBorderColor = chipColor.copy(alpha = 0.3f),
                                enabled = true,
                                selected = selected
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ─── Exam List ──────────────────────────────────
            if (exams.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Assignment,
                        title = "No exams found",
                        subtitle = if (searchQuery.isNotBlank()) "Try a different search"
                        else "Create your first exam",
                        action = {
                            Button(
                                onClick = onCreateExam,
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Exam")
                            }
                        }
                    )
                }
            } else {
                items(exams, key = { it.id }) { exam ->
                    ExamCard(exam = exam, onClick = { onExamClick(exam.id) })
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

/**
 * Individual exam card with date, name, venue, time, status, and marks info.
 */
@Composable
private fun ExamCard(
    exam: com.ims.app.data.model.Exam,
    onClick: () -> Unit
) {
    val statusColor = when (exam.status) {
        "Scheduled" -> Info
        "Ongoing" -> Warning
        "Completed" -> Success
        "Cancelled" -> Error
        else -> OnSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Date badge
            val dateParts = exam.date.split("-")
            val day = dateParts.getOrElse(2) { "??" }
            val month = when (dateParts.getOrElse(1) { "" }) {
                "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
                "05" -> "May"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Aug"
                "09" -> "Sep"; "10" -> "Oct"; "11" -> "Nov"; "12" -> "Dec"
                else -> "???"
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(day, style = MaterialTheme.typography.titleLarge, color = statusColor, fontWeight = FontWeight.Bold)
                    Text(month, style = MaterialTheme.typography.labelSmall, color = statusColor.copy(alpha = 0.7f))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exam.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (exam.venue.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnSurfaceVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(exam.venue, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${exam.startTime}–${exam.endTime}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(text = exam.status, color = statusColor)
                    Text(
                        "${exam.totalMarks} marks",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
