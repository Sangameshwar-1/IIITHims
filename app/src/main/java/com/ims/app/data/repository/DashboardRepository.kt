package com.ims.app.data.repository

import com.ims.app.data.network.ApiClient
import com.ims.app.data.model.News
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository aggregating dashboard-level data from the API.
 */
class DashboardRepository {
    
    val totalStudents: Flow<Int> = flow {
        emit(ApiClient.dashboardApi.getDashboardStats().totalStudents)
    }
    
    val activeStudents: Flow<Int> = flow {
        emit(ApiClient.dashboardApi.getDashboardStats().totalStudents)
    }
    
    val activeCourseCount: Flow<Int> = flow {
        emit(ApiClient.dashboardApi.getDashboardStats().totalCourses)
    }
    
    val upcomingExamCount: Flow<Int> = flow {
        emit(ApiClient.dashboardApi.getDashboardStats().totalFaculty)
    }
    
    val totalExamCount: Flow<Int> = flow {
        // Just return pending complaints for now
        emit(ApiClient.dashboardApi.getDashboardStats().pendingComplaints)
    }
    
    val latestNews: Flow<List<News>> = flow {
        emit(ApiClient.dashboardApi.getNews().take(5))
    }

    fun getUpcomingExams() = flow {
        emit(ApiClient.examApi.getExams().filter { it.status == "Scheduled" })
    }
    
    fun getRecentStudents() = flow {
        emit(ApiClient.studentApi.getStudents().take(5))
    }
}
