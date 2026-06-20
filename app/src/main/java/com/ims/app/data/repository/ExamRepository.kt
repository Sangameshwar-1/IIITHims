package com.ims.app.data.repository

import com.ims.app.data.network.ApiClient
import com.ims.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for examination-related data operations via REST API.
 */
class ExamRepository {

    // ─── Exams ──────────────────────────────────────────────────

    val allExams: Flow<List<Exam>> = flow {
        emit(ApiClient.examApi.getExams())
    }
    
    val totalExamCount: Flow<Int> = flow {
        emit(ApiClient.examApi.getExams().size)
    }
    
    val upcomingExamCount: Flow<Int> = flow {
        emit(ApiClient.examApi.getExams().count { it.status == "Scheduled" })
    }
    
    val upcomingExams: Flow<List<Exam>> = flow {
        emit(ApiClient.examApi.getExams().filter { it.status == "Scheduled" })
    }

    suspend fun insertExam(exam: Exam): Long {
        val created = ApiClient.examApi.createExam(exam)
        return created.id
    }
    
    suspend fun updateExam(exam: Exam) {
        ApiClient.examApi.updateExam(exam.id.toString(), exam)
    }
    
    suspend fun deleteExam(exam: Exam) {
        ApiClient.examApi.deleteExam(exam.id.toString())
    }
    
    suspend fun getExamById(id: Long): Exam? {
        return ApiClient.examApi.getExams().find { it.id == id }
    }

    fun getExamsByStatus(status: String): Flow<List<Exam>> = flow {
        emit(ApiClient.examApi.getExams().filter { it.status == status })
    }
    
    fun getExamsByCourse(courseId: Long): Flow<List<Exam>> = flow {
        emit(ApiClient.examApi.getExams().filter { it.courseId == courseId })
    }
    
    fun getExamsByBatch(batchId: Long): Flow<List<Exam>> = flow {
        emit(ApiClient.examApi.getExams().filter { it.batchId == batchId })
    }
    
    fun getExamsBySubject(subjectId: Long): Flow<List<Exam>> = flow {
        emit(ApiClient.examApi.getExams().filter { it.subjectId == subjectId })
    }
    
    fun searchExams(query: String): Flow<List<Exam>> = flow {
        val q = query.lowercase()
        emit(ApiClient.examApi.getExams().filter { it.name.lowercase().contains(q) })
    }

    // ─── Results ────────────────────────────────────────────────

    suspend fun insertResult(result: ExamResult): Long {
        val created = ApiClient.examApi.createExamResult(result)
        return created.id
    }
    
    suspend fun insertResults(results: List<ExamResult>) {
        results.forEach { ApiClient.examApi.createExamResult(it) }
    }
    
    suspend fun updateResult(result: ExamResult) {
        // Mock update for now
    }
    
    suspend fun deleteResult(result: ExamResult) {
        // Mock delete
    }

    fun getResultsByExam(examId: Long): Flow<List<ExamResult>> = flow {
        emit(ApiClient.examApi.getExamResults().filter { it.examId == examId })
    }
    
    fun getResultsByStudent(studentId: Long): Flow<List<ExamResult>> = flow {
        emit(ApiClient.examApi.getExamResults().filter { it.studentId == studentId })
    }
    
    suspend fun getResult(examId: Long, studentId: Long): ExamResult? {
        return ApiClient.examApi.getExamResults().find { it.examId == examId && it.studentId == studentId }
    }

    // ─── Statistics ─────────────────────────────────────────────

    suspend fun getExamStats(examId: Long): ExamStats {
        val results = ApiClient.examApi.getExamResults().filter { it.examId == examId }
        if (results.isEmpty()) return ExamStats()

        val marks = results.map { it.marksObtained }
        val avg = marks.average()
        val highest = marks.maxOrNull() ?: 0.0
        val lowest = marks.minOrNull() ?: 0.0
        
        val passCount = results.count { it.status == "Passed" }
        val totalAttempted = results.size
        val passPercentage = if (totalAttempted > 0) (passCount.toDouble() / totalAttempted) * 100 else 0.0

        return ExamStats(
            averageScore = avg,
            passPercentage = passPercentage,
            highestScore = highest.toDouble(),
            lowestScore = lowest.toDouble()
        )
    }

    // ─── Courses & Subjects ──────────────────────────────────────

    val activeCourses: Flow<List<Course>> = flow {
        emit(ApiClient.courseApi.getCourses().filter { it.isActive })
    }
    
    val activeSubjects: Flow<List<Subject>> = flow {
        emit(emptyList<Subject>()) // Need Subject API if actually used
    }
    
    val activeBatches: Flow<List<Batch>> = flow {
        emit(ApiClient.courseApi.getBatches().filter { it.isActive })
    }

    fun getSubjectsByCourse(courseId: Long): Flow<List<Subject>> = flow { emit(emptyList()) }
    fun getBatchesByCourse(courseId: Long): Flow<List<Batch>> = flow {
        emit(ApiClient.courseApi.getBatches().filter { it.courseId == courseId })
    }
    
    suspend fun getCourseById(id: Long): Course? {
        return ApiClient.courseApi.getCourses().find { it.id == id }
    }
    
    suspend fun getSubjectById(id: Long): Subject? = null
}
