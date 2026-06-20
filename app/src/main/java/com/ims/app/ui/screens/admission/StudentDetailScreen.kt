package com.ims.app.ui.screens.admission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ims.app.ui.components.StatusChip
import com.ims.app.ui.theme.*

/**
 * Detailed student profile view showing personal, contact, guardian, and academic info.
 * Supports editing and deleting students with confirmation dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    viewModel: AdmissionViewModel,
    studentId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        viewModel.loadStudentById(studentId)
    }

    val student by viewModel.selectedStudent.collectAsState()
    val guardians by viewModel.guardians.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Student Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { student?.let { onEdit(it.id) } }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Primary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Error)
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
        student?.let { s ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── Profile Header ─────────────────────────
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Brush.linearGradient(GradientPrimary))
                                .fillMaxWidth()
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${s.firstName.firstOrNull() ?: ""}${s.lastName.firstOrNull() ?: ""}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 28.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "${s.firstName} ${s.lastName}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    s.studentId,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val statusColor = when (s.status) {
                                    "Active" -> Success
                                    "Graduated" -> Info
                                    "Dropped" -> Error
                                    else -> Warning
                                }
                                StatusChip(text = s.status, color = statusColor)
                            }
                        }
                    }
                }

                // ─── Personal Details ───────────────────────
                item {
                    InfoSection(title = "Personal Details", icon = Icons.Filled.Person) {
                        InfoRow("Date of Birth", s.dateOfBirth)
                        InfoRow("Gender", s.gender)
                        InfoRow("Blood Group", s.bloodGroup)
                        InfoRow("Nationality", s.nationality)
                        InfoRow("Category", s.category)
                    }
                }

                // ─── Contact Information ────────────────────
                item {
                    InfoSection(title = "Contact Information", icon = Icons.Filled.ContactPhone) {
                        InfoRow("Email", s.email)
                        InfoRow("Phone", s.phone)
                        InfoRow("Address", s.address)
                        InfoRow("City", "${s.city}, ${s.state}")
                        InfoRow("PIN Code", s.pinCode)
                    }
                }

                // ─── Guardian Details ───────────────────────
                if (guardians.isNotEmpty()) {
                    item {
                        Text(
                            "Guardian Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(guardians) { guardian ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Secondary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Person, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(guardian.name, style = MaterialTheme.typography.titleSmall, color = OnSurface)
                                        Text(guardian.relation, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
                                    }
                                    if (guardian.isEmergencyContact) {
                                        StatusChip(text = "Emergency", color = Warning)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                InfoRow("Phone", guardian.phone)
                                if (guardian.email.isNotBlank()) InfoRow("Email", guardian.email)
                                if (guardian.occupation.isNotBlank()) InfoRow("Occupation", guardian.occupation)
                            }
                        }
                    }
                }

                // ─── Academic Information ───────────────────
                item {
                    InfoSection(title = "Academic Information", icon = Icons.Filled.School) {
                        InfoRow("Admission Date", s.admissionDate)
                        InfoRow("Course ID", s.courseId.toString())
                        InfoRow("Batch ID", s.batchId.toString())
                    }
                }

                // ─── Previous Education ─────────────────────
                if (s.previousSchool.isNotBlank()) {
                    item {
                        InfoSection(title = "Previous Education", icon = Icons.Filled.HistoryEdu) {
                            InfoRow("School", s.previousSchool)
                            InfoRow("Grade", s.previousGrade)
                            InfoRow("Percentage/CGPA", s.previousPercentage)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Student", color = OnSurface) },
            text = { Text("Are you sure you want to remove this student? This action cannot be undone.", color = OnSurfaceVariant) },
            confirmButton = {
                TextButton(
                    onClick = {
                        student?.let { viewModel.deleteStudent(it) { onBack() } }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = OnSurfaceVariant)
                }
            },
            containerColor = SurfaceElevated,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/**
 * Reusable info section card with title and icon.
 */
@Composable
private fun InfoSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Single row of label-value pair for detail views.
 */
@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.weight(0.4f)
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}
