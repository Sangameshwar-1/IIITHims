package com.ims.app.ui.screens.examination

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ims.app.data.model.Exam
import com.ims.app.ui.components.ImsTextField
import com.ims.app.ui.screens.admission.DropdownField
import com.ims.app.ui.theme.*

/**
 * Form for creating or editing an exam.
 * Includes course/batch/subject dropdowns and validation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamScreen(
    viewModel: ExaminationViewModel,
    examId: Long = -1L,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val isEditing = examId != -1L
    val courses by viewModel.activeCourses.collectAsState()
    val subjects by viewModel.activeSubjects.collectAsState()
    val batches by viewModel.activeBatches.collectAsState()

    // Form state
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Marks") }
    var selectedCourseId by remember { mutableLongStateOf(0L) }
    var selectedSubjectId by remember { mutableLongStateOf(0L) }
    var selectedBatchId by remember { mutableLongStateOf(0L) }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var totalMarks by remember { mutableStateOf("100") }
    var passingMarks by remember { mutableStateOf("40") }
    var venue by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }

    // Load existing exam if editing
    LaunchedEffect(examId) {
        if (isEditing) {
            viewModel.loadExamById(examId)
        }
    }

    val selectedExam by viewModel.selectedExam.collectAsState()
    LaunchedEffect(selectedExam) {
        selectedExam?.let { e ->
            name = e.name; type = e.type; date = e.date
            startTime = e.startTime; endTime = e.endTime
            totalMarks = e.totalMarks.toString(); passingMarks = e.passingMarks.toString()
            venue = e.venue; description = e.description
            selectedCourseId = e.courseId; selectedSubjectId = e.subjectId
            selectedBatchId = e.batchId
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Exam" else "Create Exam",
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
            // ─── Exam Details ───────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Exam Details", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                        ImsTextField(
                            value = name,
                            onValueChange = { name = it; nameError = false },
                            label = "Exam Name",
                            leadingIcon = Icons.Filled.Assignment,
                            isError = nameError,
                            errorMessage = if (nameError) "Name is required" else null
                        )

                        DropdownField(
                            value = type,
                            options = listOf("Marks", "Grade", "GPA", "CCE", "CWA"),
                            onSelected = { type = it },
                            label = "Exam Type"
                        )

                        ImsTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Description (Optional)",
                            leadingIcon = Icons.Filled.Description,
                            singleLine = false
                        )
                    }
                }
            }

            // ─── Course & Subject ───────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Course & Subject", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                        DropdownField(
                            value = courses.find { it.id == selectedCourseId }?.name ?: "Select Course",
                            options = courses.map { it.name },
                            onSelected = { name ->
                                selectedCourseId = courses.find { it.name == name }?.id ?: 0
                            },
                            label = "Course"
                        )

                        DropdownField(
                            value = batches.find { it.id == selectedBatchId }?.name ?: "Select Batch",
                            options = batches.filter { selectedCourseId == 0L || it.courseId == selectedCourseId }.map { it.name },
                            onSelected = { name ->
                                selectedBatchId = batches.find { it.name == name }?.id ?: 0
                            },
                            label = "Batch"
                        )

                        DropdownField(
                            value = subjects.find { it.id == selectedSubjectId }?.name ?: "Select Subject",
                            options = subjects.filter { selectedCourseId == 0L || it.courseId == selectedCourseId }.map { it.name },
                            onSelected = { name ->
                                selectedSubjectId = subjects.find { it.name == name }?.id ?: 0
                            },
                            label = "Subject"
                        )
                    }
                }
            }

            // ─── Schedule ───────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Schedule", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                        ImsTextField(
                            value = date,
                            onValueChange = { date = it; dateError = false },
                            label = "Date (YYYY-MM-DD)",
                            leadingIcon = Icons.Filled.CalendarToday,
                            isError = dateError,
                            errorMessage = if (dateError) "Date is required" else null
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ImsTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                label = "Start (HH:mm)",
                                leadingIcon = Icons.Filled.AccessTime,
                                modifier = Modifier.weight(1f)
                            )
                            ImsTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                label = "End (HH:mm)",
                                leadingIcon = Icons.Filled.AccessTime,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        ImsTextField(
                            value = venue,
                            onValueChange = { venue = it },
                            label = "Venue",
                            leadingIcon = Icons.Filled.LocationOn
                        )
                    }
                }
            }

            // ─── Marks ──────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Marks Configuration", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ImsTextField(
                                value = totalMarks,
                                onValueChange = { totalMarks = it },
                                label = "Total Marks",
                                leadingIcon = Icons.Filled.Grade,
                                modifier = Modifier.weight(1f)
                            )
                            ImsTextField(
                                value = passingMarks,
                                onValueChange = { passingMarks = it },
                                label = "Passing Marks",
                                leadingIcon = Icons.Filled.CheckCircle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ─── Save Button ────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        nameError = name.isBlank()
                        dateError = date.isBlank()

                        if (!nameError && !dateError) {
                            val exam = Exam(
                                id = if (isEditing) examId else 0,
                                name = name, type = type, description = description,
                                subjectId = selectedSubjectId, courseId = selectedCourseId,
                                batchId = selectedBatchId, date = date,
                                startTime = startTime, endTime = endTime,
                                totalMarks = totalMarks.toIntOrNull() ?: 100,
                                passingMarks = passingMarks.toIntOrNull() ?: 40,
                                venue = venue,
                                status = if (isEditing) (selectedExam?.status ?: "Scheduled") else "Scheduled"
                            )
                            if (isEditing) {
                                viewModel.updateExam(exam)
                            } else {
                                viewModel.insertExam(exam)
                            }
                            onSaved()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditing) "Update Exam" else "Create Exam",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
