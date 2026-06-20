package com.ims.app.ui.screens.examination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ims.app.ImsApplication
import com.ims.app.data.model.*
import com.ims.app.data.repository.ExamRepository
import com.ims.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Examination module.
 * Manages exam CRUD, result recording, and statistical reporting.
 */
class ExaminationViewModel(
    private val examRepository: ExamRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    // ─── Search & Filter ────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    // ─── Data Flows ─────────────────────────────────────────────
    val allExams: StateFlow<List<Exam>> = examRepository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredExams: StateFlow<List<Exam>> = combine(
        examRepository.allExams,
        _searchQuery,
        _statusFilter
    ) { exams, query, status ->
        exams.filter { exam ->
            val matchesQuery = query.isBlank() ||
                exam.name.contains(query, ignoreCase = true) ||
                exam.description.contains(query, ignoreCase = true)
            val matchesStatus = status == "All" || exam.status == status
            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCourses: StateFlow<List<Course>> = examRepository.activeCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSubjects: StateFlow<List<Subject>> = examRepository.activeSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBatches: StateFlow<List<Batch>> = examRepository.activeBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<Student>> = studentRepository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExamCount: StateFlow<Int> = examRepository.totalExamCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ─── Selected Exam State ────────────────────────────────────
    private val _selectedExam = MutableStateFlow<Exam?>(null)
    val selectedExam: StateFlow<Exam?> = _selectedExam.asStateFlow()

    private val _examResults = MutableStateFlow<List<ExamResult>>(emptyList())
    val examResults: StateFlow<List<ExamResult>> = _examResults.asStateFlow()

    private val _examStats = MutableStateFlow(ExamStats())
    val examStats: StateFlow<ExamStats> = _examStats.asStateFlow()

    // ─── Actions ────────────────────────────────────────────────

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onStatusFilterChanged(status: String) { _statusFilter.value = status }

    fun loadExamById(id: Long) {
        viewModelScope.launch {
            _selectedExam.value = examRepository.getExamById(id)
        }
    }

    fun loadResultsByExam(examId: Long) {
        viewModelScope.launch {
            examRepository.getResultsByExam(examId).collect { results ->
                _examResults.value = results
            }
        }
    }

    fun loadExamStats(examId: Long) {
        viewModelScope.launch {
            _examStats.value = examRepository.getExamStats(examId)
        }
    }

    fun insertExam(exam: Exam, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = examRepository.insertExam(exam)
            onComplete(id)
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch { examRepository.updateExam(exam) }
    }

    fun deleteExam(exam: Exam, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            examRepository.deleteExam(exam)
            onComplete()
        }
    }

    fun insertResult(result: ExamResult) {
        viewModelScope.launch {
            examRepository.insertResult(result)
            loadResultsByExam(result.examId)
            loadExamStats(result.examId)
        }
    }

    fun insertResults(results: List<ExamResult>) {
        viewModelScope.launch { examRepository.insertResults(results) }
    }

    fun updateResult(result: ExamResult) {
        viewModelScope.launch {
            examRepository.updateResult(result)
            loadResultsByExam(result.examId)
            loadExamStats(result.examId)
        }
    }

    fun getSubjectsByCourse(courseId: Long): Flow<List<Subject>> = examRepository.getSubjectsByCourse(courseId)
    fun getBatchesByCourse(courseId: Long): Flow<List<Batch>> = examRepository.getBatchesByCourse(courseId)

    // ─── Factory ────────────────────────────────────────────────
    class Factory(private val app: ImsApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExaminationViewModel::class.java)) {
                return ExaminationViewModel(app.examRepository, app.studentRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
