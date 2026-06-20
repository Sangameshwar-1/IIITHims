package com.ims.app.ui.screens.admission

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ims.app.data.model.Guardian
import com.ims.app.data.model.Student
import com.ims.app.ui.components.ImsTextField
import com.ims.app.ui.theme.*

/**
 * Multi-step admission form: Personal Info → Guardian Info → Education History.
 * Supports both creating new students and editing existing ones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionFormScreen(
    viewModel: AdmissionViewModel,
    studentId: Long = -1L,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val courses by viewModel.activeCourses.collectAsState()
    val batches by viewModel.activeBatches.collectAsState()
    val isEditing = studentId != -1L

    // ─── Form State ─────────────────────────────────────────────
    var currentStep by remember { mutableIntStateOf(0) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("Indian") }
    var category by remember { mutableStateOf("General") }

    // Guardian state
    var guardianName by remember { mutableStateOf("") }
    var guardianRelation by remember { mutableStateOf("Father") }
    var guardianPhone by remember { mutableStateOf("") }
    var guardianEmail by remember { mutableStateOf("") }
    var guardianOccupation by remember { mutableStateOf("") }
    var isEmergencyContact by remember { mutableStateOf(true) }
    val addedGuardians = remember { mutableStateListOf<Guardian>() }

    // Education state
    var previousSchool by remember { mutableStateOf("") }
    var previousGrade by remember { mutableStateOf("") }
    var previousPercentage by remember { mutableStateOf("") }
    var selectedCourseId by remember { mutableLongStateOf(0L) }
    var selectedBatchId by remember { mutableLongStateOf(0L) }

    // Load existing student data if editing
    LaunchedEffect(studentId) {
        if (isEditing) {
            viewModel.loadStudentById(studentId)
        }
    }

    val selectedStudent by viewModel.selectedStudent.collectAsState()
    LaunchedEffect(selectedStudent) {
        selectedStudent?.let { s ->
            firstName = s.firstName; lastName = s.lastName; email = s.email
            phone = s.phone; dateOfBirth = s.dateOfBirth; gender = s.gender
            address = s.address; city = s.city; state = s.state; pinCode = s.pinCode
            bloodGroup = s.bloodGroup; nationality = s.nationality; category = s.category
            previousSchool = s.previousSchool; previousGrade = s.previousGrade
            previousPercentage = s.previousPercentage
            selectedCourseId = s.courseId; selectedBatchId = s.batchId
        }
    }

    val steps = listOf("Personal Info", "Guardian Info", "Education")

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Student" else "New Admission",
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
            // ─── Step Indicator ──────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { index, label ->
                        val isActive = index == currentStep
                        val isCompleted = index < currentStep
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isActive -> Primary
                                            isCompleted -> Success
                                            else -> SurfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("${index + 1}", color = if (isActive) Color.White else OnSurfaceVariant, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isActive || isCompleted) OnSurface else OnSurfaceVariant.copy(alpha = 0.5f),
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                        if (index < steps.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .weight(0.5f)
                                    .background(if (isCompleted) Success else Divider)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ─── Step Content ────────────────────────────────
            when (currentStep) {
                0 -> {
                    // Personal Information
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                            modifier = Modifier.animateContentSize()
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Personal Information", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ImsTextField(value = firstName, onValueChange = { firstName = it }, label = "First Name", leadingIcon = Icons.Filled.Person, modifier = Modifier.weight(1f))
                                    ImsTextField(value = lastName, onValueChange = { lastName = it }, label = "Last Name", modifier = Modifier.weight(1f))
                                }
                                ImsTextField(value = email, onValueChange = { email = it }, label = "Email Address", leadingIcon = Icons.Filled.Email)
                                ImsTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", leadingIcon = Icons.Filled.Phone)

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ImsTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = "Date of Birth (YYYY-MM-DD)", modifier = Modifier.weight(1f), leadingIcon = Icons.Filled.CalendarToday)
                                    DropdownField(value = gender, options = listOf("Male", "Female", "Other"), onSelected = { gender = it }, label = "Gender", modifier = Modifier.weight(1f))
                                }

                                ImsTextField(value = address, onValueChange = { address = it }, label = "Address", leadingIcon = Icons.Filled.Home, singleLine = false)

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ImsTextField(value = city, onValueChange = { city = it }, label = "City", modifier = Modifier.weight(1f))
                                    ImsTextField(value = state, onValueChange = { state = it }, label = "State", modifier = Modifier.weight(1f))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ImsTextField(value = pinCode, onValueChange = { pinCode = it }, label = "PIN Code", modifier = Modifier.weight(1f))
                                    DropdownField(value = bloodGroup, options = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"), onSelected = { bloodGroup = it }, label = "Blood Group", modifier = Modifier.weight(1f))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ImsTextField(value = nationality, onValueChange = { nationality = it }, label = "Nationality", modifier = Modifier.weight(1f))
                                    DropdownField(value = category, options = listOf("General", "OBC", "SC", "ST", "EWS"), onSelected = { category = it }, label = "Category", modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Guardian Information
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                            modifier = Modifier.animateContentSize()
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Add Guardian", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                                ImsTextField(value = guardianName, onValueChange = { guardianName = it }, label = "Guardian Name", leadingIcon = Icons.Filled.Person)

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    DropdownField(value = guardianRelation, options = listOf("Father", "Mother", "Guardian", "Sibling", "Other"), onSelected = { guardianRelation = it }, label = "Relation", modifier = Modifier.weight(1f))
                                    ImsTextField(value = guardianPhone, onValueChange = { guardianPhone = it }, label = "Phone", modifier = Modifier.weight(1f))
                                }

                                ImsTextField(value = guardianEmail, onValueChange = { guardianEmail = it }, label = "Email (Optional)", leadingIcon = Icons.Filled.Email)
                                ImsTextField(value = guardianOccupation, onValueChange = { guardianOccupation = it }, label = "Occupation", leadingIcon = Icons.Filled.Work)

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isEmergencyContact,
                                        onCheckedChange = { isEmergencyContact = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Emergency Contact", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                                }

                                Button(
                                    onClick = {
                                        if (guardianName.isNotBlank() && guardianPhone.isNotBlank()) {
                                            addedGuardians.add(
                                                Guardian(
                                                    studentId = 0, name = guardianName,
                                                    relation = guardianRelation, phone = guardianPhone,
                                                    email = guardianEmail, occupation = guardianOccupation,
                                                    isEmergencyContact = isEmergencyContact
                                                )
                                            )
                                            guardianName = ""; guardianPhone = ""; guardianEmail = ""
                                            guardianOccupation = ""; isEmergencyContact = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Guardian", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Show added guardians
                    if (addedGuardians.isNotEmpty()) {
                        item {
                            Text("Added Guardians (${addedGuardians.size})", style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.SemiBold)
                        }
                        addedGuardians.forEachIndexed { index, guardian ->
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(guardian.name, style = MaterialTheme.typography.titleSmall, color = OnSurface)
                                            Text("${guardian.relation} • ${guardian.phone}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.6f))
                                            if (guardian.isEmergencyContact) {
                                                Text("🚨 Emergency Contact", style = MaterialTheme.typography.labelSmall, color = Warning)
                                            }
                                        }
                                        IconButton(onClick = { addedGuardians.removeAt(index) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = Error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Education History
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                            modifier = Modifier.animateContentSize()
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Previous Education", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

                                ImsTextField(value = previousSchool, onValueChange = { previousSchool = it }, label = "Previous School/Institute", leadingIcon = Icons.Filled.School)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ImsTextField(value = previousGrade, onValueChange = { previousGrade = it }, label = "Last Grade/Class", modifier = Modifier.weight(1f))
                                    ImsTextField(value = previousPercentage, onValueChange = { previousPercentage = it }, label = "Percentage/CGPA", modifier = Modifier.weight(1f))
                                }

                                HorizontalDivider(color = Divider)

                                Text("Course & Batch Selection", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)

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
                            }
                        }
                    }
                }
            }

            // ─── Navigation Buttons ─────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Border, Border))
                            )
                        ) {
                            Text("Back", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < 2) {
                                currentStep++
                            } else {
                                // Save student
                                val student = Student(
                                    id = if (isEditing) studentId else 0,
                                    studentId = if (isEditing) (viewModel.selectedStudent.value?.studentId ?: "") else viewModel.generateStudentId(),
                                    firstName = firstName, lastName = lastName, email = email, phone = phone,
                                    dateOfBirth = dateOfBirth, gender = gender, address = address, city = city,
                                    state = state, pinCode = pinCode, bloodGroup = bloodGroup,
                                    nationality = nationality, category = category,
                                    previousSchool = previousSchool, previousGrade = previousGrade,
                                    previousPercentage = previousPercentage,
                                    courseId = selectedCourseId, batchId = selectedBatchId,
                                    admissionDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                                    status = "Active"
                                )

                                if (isEditing) {
                                    viewModel.updateStudent(student)
                                    onSaved()
                                } else {
                                    viewModel.insertStudent(student) { newId ->
                                        addedGuardians.forEach { guardian ->
                                            viewModel.insertGuardian(guardian.copy(studentId = newId))
                                        }
                                        onSaved()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(if (currentStep > 0) 1f else 1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(
                            if (currentStep < 2) "Next" else if (isEditing) "Update" else "Submit",
                            fontWeight = FontWeight.SemiBold
                        )
                        if (currentStep < 2) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

/**
 * Reusable dropdown field with ExposedDropdownMenu styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                cursorColor = Primary
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceElevated)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = OnSurface) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
