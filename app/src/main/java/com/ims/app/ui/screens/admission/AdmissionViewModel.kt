package com.ims.app.ui.screens.admission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ims.app.ImsApplication
import com.ims.app.data.model.*
import com.ims.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Student Admission module.
 * Manages student CRUD, guardian management, search, and filtering.
 */
class AdmissionViewModel(private val repository: StudentRepository) : ViewModel() {

    // ─── Search & Filter State ──────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    // ─── Data Flows ─────────────────────────────────────────────
    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredStudents: StateFlow<List<Student>> = combine(
        repository.allStudents,
        _searchQuery,
        _statusFilter
    ) { students, query, status ->
        students.filter { student ->
            val matchesQuery = query.isBlank() ||
                student.firstName.contains(query, ignoreCase = true) ||
                student.lastName.contains(query, ignoreCase = true) ||
                student.studentId.contains(query, ignoreCase = true) ||
                student.email.contains(query, ignoreCase = true)
            val matchesStatus = status == "All" || student.status == status
            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCourses: StateFlow<List<Course>> = repository.activeCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBatches: StateFlow<List<Batch>> = repository.activeBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ─── Selected Student State ─────────────────────────────────
    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent.asStateFlow()

    private val _guardians = MutableStateFlow<List<Guardian>>(emptyList())
    val guardians: StateFlow<List<Guardian>> = _guardians.asStateFlow()

    // ─── Actions ────────────────────────────────────────────────

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(status: String) {
        _statusFilter.value = status
    }

    fun loadStudentById(id: Long) {
        viewModelScope.launch {
            _selectedStudent.value = repository.getStudentById(id)
            repository.getGuardiansByStudent(id).collect { list ->
                _guardians.value = list
            }
        }
    }

    fun insertStudent(student: Student, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertStudent(student)
            onComplete(id)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteGuardiansByStudent(student.id)
            repository.deleteStudent(student)
            onComplete()
        }
    }

    fun insertGuardian(guardian: Guardian) {
        viewModelScope.launch {
            repository.insertGuardian(guardian)
        }
    }

    fun deleteGuardian(guardian: Guardian) {
        viewModelScope.launch {
            repository.deleteGuardian(guardian)
        }
    }

    fun generateStudentId(): String {
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val count = totalCount.value
        return "STU-$year-${String.format("%04d", count + 1)}"
    }

    fun getBatchesByCourse(courseId: Long): Flow<List<Batch>> = repository.getBatchesByCourse(courseId)

    // ─── Factory ────────────────────────────────────────────────
    class Factory(private val app: ImsApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdmissionViewModel::class.java)) {
                return AdmissionViewModel(app.studentRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
