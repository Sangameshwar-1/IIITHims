package com.ims.app.ui.screens.admission

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ims.app.ui.components.*
import com.ims.app.ui.theme.*

/**
 * Displays the list of all students with search, filtering, and navigation to details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionListScreen(
    viewModel: AdmissionViewModel,
    onStudentClick: (Long) -> Unit,
    onNewAdmission: () -> Unit
) {
    val students by viewModel.filteredStudents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()

    val filterOptions = listOf("All", "Active", "Graduated", "Dropped", "Suspended")

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            GradientFab(onClick = onNewAdmission)
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
                Column {
                    Text(
                        text = "Student Admissions",
                        style = MaterialTheme.typography.displaySmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalCount students enrolled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ─── Search Bar ─────────────────────────────────
            item {
                ImsSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search by name, ID, or email…"
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
                                selectedContainerColor = Primary.copy(alpha = 0.15f),
                                selectedLabelColor = Primary,
                                containerColor = SurfaceVariant,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = Border,
                                selectedBorderColor = Primary.copy(alpha = 0.3f),
                                enabled = true,
                                selected = selected
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ─── Student List ───────────────────────────────
            if (students.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.PersonSearch,
                        title = "No students found",
                        subtitle = if (searchQuery.isNotBlank()) "Try a different search term"
                        else "Add a new student to get started",
                        action = {
                            Button(
                                onClick = onNewAdmission,
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Admission")
                            }
                        }
                    )
                }
            } else {
                items(students, key = { it.id }) { student ->
                    val statusColor = when (student.status) {
                        "Active" -> Success
                        "Graduated" -> Info
                        "Dropped" -> Error
                        "Suspended" -> Warning
                        else -> OnSurfaceVariant
                    }

                    ImsListItem(
                        title = "${student.firstName} ${student.lastName}",
                        subtitle = "${student.studentId} • ${student.email}",
                        leadingIcon = Icons.Filled.Person,
                        leadingIconColor = Primary,
                        onClick = { onStudentClick(student.id) },
                        trailingContent = {
                            StatusChip(text = student.status, color = statusColor)
                        }
                    )
                }
            }

            // Bottom spacer for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
