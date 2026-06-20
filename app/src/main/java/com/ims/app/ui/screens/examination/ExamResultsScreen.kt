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
import com.ims.app.data.model.ExamResult
import com.ims.app.ui.components.*
import com.ims.app.ui.screens.admission.DropdownField
import com.ims.app.ui.theme.*

/**
 * Displays results for a specific exam with stats, result list, and add-result dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultsScreen(
    viewModel: ExaminationViewModel,
    examId: Long,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(examId) {
        viewModel.loadExamById(examId)
        viewModel.loadResultsByExam(examId)
        viewModel.loadExamStats(examId)
    }

    val exam by viewModel.selectedExam.collectAsState()
    val results by viewModel.examResults.collectAsState()
    val stats by viewModel.examStats.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Exam Results", fontWeight = FontWeight.SemiBold) },
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
        },
        floatingActionButton = {
            GradientFab(
                onClick = { showAddDialog = true },
                icon = Icons.Filled.AddChart
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ─── Exam Info Header ───────────────────────────
            exam?.let { e ->
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Brush.linearGradient(GradientPrimary))
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            Column {
                                Text(e.name, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.CalendarToday, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(e.date, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Grade, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("${e.totalMarks} marks", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                StatusChip(text = e.status, color = Color.White)
                            }
                        }
                    }
                }
            }

            // ─── Stats Row ──────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatCard("Average", String.format("%.1f", stats.averageScore), Info, Modifier.weight(1f))
                    MiniStatCard("Pass %", String.format("%.0f%%", stats.passPercentage), Success, Modifier.weight(1f))
                    MiniStatCard("Highest", String.format("%.0f", stats.highestScore), Primary, Modifier.weight(1f))
                    MiniStatCard("Lowest", String.format("%.0f", stats.lowestScore), Warning, Modifier.weight(1f))
                }
            }

            // ─── Results Header ─────────────────────────────
            item {
                SectionHeader(
                    title = "Results (${results.size})",
                    actionText = if (results.isNotEmpty()) "Export" else null,
                    onActionClick = { /* Export action placeholder */ }
                )
            }

            // ─── Result List ────────────────────────────────
            if (results.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.AssignmentTurnedIn,
                        title = "No results yet",
                        subtitle = "Add student results using the + button"
                    )
                }
            } else {
                items(results, key = { it.id }) { result ->
                    val student = allStudents.find { it.id == result.studentId }
                    val statusColor = when (result.status) {
                        "Passed" -> Success
                        "Failed" -> Error
                        "Absent" -> OnSurfaceVariant
                        else -> Warning
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
                            // Student avatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(statusColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    student?.firstName?.firstOrNull()?.toString() ?: "?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    student?.let { "${it.firstName} ${it.lastName}" } ?: "Student #${result.studentId}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = OnSurface
                                )
                                Text(
                                    student?.studentId ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${result.marksObtained.toInt()}/${exam?.totalMarks ?: 100}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                                if (result.grade.isNotBlank()) {
                                    Text(result.grade, style = MaterialTheme.typography.labelSmall, color = statusColor.copy(alpha = 0.7f))
                                }
                                StatusChip(text = result.status, color = statusColor)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // ─── Add Result Dialog ──────────────────────────────────────
    if (showAddDialog) {
        AddResultDialog(
            students = allStudents,
            totalMarks = exam?.totalMarks ?: 100,
            passingMarks = exam?.passingMarks ?: 40,
            onDismiss = { showAddDialog = false },
            onSave = { result ->
                viewModel.insertResult(result.copy(examId = examId))
                showAddDialog = false
            }
        )
    }
}

/**
 * Mini stat card for the stats row.
 */
@Composable
private fun MiniStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
        }
    }
}

/**
 * Dialog for adding a student's exam result.
 */
@Composable
private fun AddResultDialog(
    students: List<com.ims.app.data.model.Student>,
    totalMarks: Int,
    passingMarks: Int,
    onDismiss: () -> Unit,
    onSave: (ExamResult) -> Unit
) {
    var selectedStudentId by remember { mutableLongStateOf(0L) }
    var marks by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Result", color = OnSurface, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DropdownField(
                    value = students.find { it.id == selectedStudentId }?.let { "${it.firstName} ${it.lastName}" } ?: "Select Student",
                    options = students.map { "${it.firstName} ${it.lastName}" },
                    onSelected = { name ->
                        selectedStudentId = students.find { "${it.firstName} ${it.lastName}" == name }?.id ?: 0
                    },
                    label = "Student"
                )

                ImsTextField(
                    value = marks,
                    onValueChange = { marks = it },
                    label = "Marks Obtained (out of $totalMarks)",
                    leadingIcon = Icons.Filled.Grade
                )

                DropdownField(
                    value = grade.ifBlank { "Select Grade" },
                    options = listOf("A+", "A", "B+", "B", "C+", "C", "D", "F"),
                    onSelected = { grade = it },
                    label = "Grade"
                )

                ImsTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = "Remarks (Optional)",
                    leadingIcon = Icons.Filled.Note
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val marksValue = marks.toDoubleOrNull() ?: 0.0
                    val status = when {
                        marksValue <= 0 -> "Absent"
                        marksValue >= passingMarks -> "Passed"
                        else -> "Failed"
                    }
                    onSave(
                        ExamResult(
                            examId = 0, // Will be set by caller
                            studentId = selectedStudentId,
                            marksObtained = marksValue,
                            grade = grade,
                            remarks = remarks,
                            status = status,
                            evaluatedAt = System.currentTimeMillis()
                        )
                    )
                }
            ) {
                Text("Save", color = Primary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceElevated,
        shape = RoundedCornerShape(20.dp)
    )
}
