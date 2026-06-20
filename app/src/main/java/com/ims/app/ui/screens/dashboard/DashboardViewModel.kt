package com.ims.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ims.app.ImsApplication
import com.ims.app.data.model.Exam
import com.ims.app.data.model.News
import com.ims.app.data.model.Student
import com.ims.app.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard screen.
 * Aggregates key metrics, upcoming exams, latest news, and recent students
 * from [DashboardRepository] and exposes them as observable [StateFlow]s.
 *
 * Uses a [Factory] pattern for construction since Hilt is not in use.
 */
class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    // ── Search ──────────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── User role ───────────────────────────────────────────────────
    private val _currentUserRole = MutableStateFlow("Admin")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    // ── Scalar metrics ──────────────────────────────────────────────
    val totalStudents: StateFlow<Int> = repository.totalStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val activeCourses: StateFlow<Int> = repository.activeCourseCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val upcomingExamCount: StateFlow<Int> = repository.upcomingExamCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // ── Collection data (raw, unfiltered) ───────────────────────────
    private val _allNews: StateFlow<List<News>> = repository.latestNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _allUpcomingExams: StateFlow<List<Exam>> = repository.getUpcomingExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _allStudents: StateFlow<List<Student>> = repository.getRecentStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Filtered / derived data exposed to UI ───────────────────────

    /** Latest news filtered by the current search query. */
    val latestNews: StateFlow<List<News>> = combine(_allNews, _searchQuery) { news, query ->
        if (query.isBlank()) news
        else news.filter { item ->
            item.title.contains(query, ignoreCase = true) ||
            item.content.contains(query, ignoreCase = true) ||
            item.category.contains(query, ignoreCase = true) ||
            item.author.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Upcoming exams filtered by the current search query. */
    val upcomingExams: StateFlow<List<Exam>> = combine(_allUpcomingExams, _searchQuery) { exams, query ->
        if (query.isBlank()) exams
        else exams.filter { exam ->
            exam.name.contains(query, ignoreCase = true) ||
            exam.venue.contains(query, ignoreCase = true) ||
            exam.type.contains(query, ignoreCase = true) ||
            exam.description.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Five most-recent students filtered by the current search query. */
    val recentStudents: StateFlow<List<Student>> = combine(_allStudents, _searchQuery) { students, query ->
        val filtered = if (query.isBlank()) students
        else students.filter { s ->
            s.firstName.contains(query, ignoreCase = true) ||
            s.lastName.contains(query, ignoreCase = true) ||
            s.studentId.contains(query, ignoreCase = true) ||
            s.email.contains(query, ignoreCase = true) ||
            s.city.contains(query, ignoreCase = true)
        }
        filtered.take(5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Actions ─────────────────────────────────────────────────────

    /** Update the global search query; all exposed lists react automatically. */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // ── Factory ─────────────────────────────────────────────────────

    /**
     * [ViewModelProvider.Factory] that pulls [DashboardRepository] from
     * the application-level dependency graph ([ImsApplication]).
     */
    class Factory(private val application: ImsApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(application.dashboardRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
