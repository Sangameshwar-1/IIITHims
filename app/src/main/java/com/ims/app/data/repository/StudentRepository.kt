package com.ims.app.data.repository

import com.ims.app.data.network.ApiClient
import com.ims.app.data.model.Batch
import com.ims.app.data.model.Course
import com.ims.app.data.model.Guardian
import com.ims.app.data.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

/**
 * Repository for student-related data operations via REST API.
 */
class StudentRepository {

    val allStudents: Flow<List<Student>> = flow {
        emit(ApiClient.studentApi.getStudents())
    }
    
    val totalCount: Flow<Int> = flow {
        emit(ApiClient.studentApi.getStudents().size)
    }
    
    val activeCount: Flow<Int> = flow {
        emit(ApiClient.studentApi.getStudents().count { it.status == "Active" })
    }

    suspend fun insertStudent(student: Student): Long {
        val created = ApiClient.studentApi.createStudent(student)
        return created.id
    }
    
    suspend fun updateStudent(student: Student) {
        ApiClient.studentApi.updateStudent(student.id.toString(), student)
    }
    
    suspend fun deleteStudent(student: Student) {
        ApiClient.studentApi.deleteStudent(student.id.toString())
    }
    
    suspend fun getStudentById(id: Long): Student? {
        return try { ApiClient.studentApi.getStudentById(id.toString()) } catch (e: Exception) { null }
    }
    
    suspend fun getStudentByStudentId(studentId: String): Student? {
        return ApiClient.studentApi.getStudents().find { it.studentId == studentId }
    }

    fun getStudentsByStatus(status: String): Flow<List<Student>> = flow {
        emit(ApiClient.studentApi.getStudents().filter { it.status == status })
    }
    
    fun getStudentsByBatch(batchId: Long): Flow<List<Student>> = flow {
        emit(ApiClient.studentApi.getStudents().filter { it.batchId == batchId })
    }
    
    fun getStudentsByCourse(courseId: Long): Flow<List<Student>> = flow {
        emit(ApiClient.studentApi.getStudents().filter { it.courseId == courseId })
    }
    
    fun searchStudents(query: String): Flow<List<Student>> = flow {
        val q = query.lowercase()
        emit(ApiClient.studentApi.getStudents().filter { 
            it.firstName.lowercase().contains(q) || 
            it.lastName.lowercase().contains(q) || 
            it.studentId.lowercase().contains(q) 
        })
    }
    
    fun getRecentAdmissionCount(since: String): Flow<Int> = flow {
        emit(ApiClient.studentApi.getStudents().count { it.admissionDate >= since })
    }

    fun advancedSearch(
        status: String = "",
        courseId: Long = 0,
        batchId: Long = 0,
        category: String = "",
        gender: String = ""
    ): Flow<List<Student>> = flow {
        var list = ApiClient.studentApi.getStudents()
        if (status.isNotEmpty()) list = list.filter { it.status == status }
        if (courseId != 0L) list = list.filter { it.courseId == courseId }
        if (batchId != 0L) list = list.filter { it.batchId == batchId }
        if (category.isNotEmpty()) list = list.filter { it.category == category }
        if (gender.isNotEmpty()) list = list.filter { it.gender == gender }
        emit(list)
    }

    suspend fun generateStudentId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val count = ApiClient.studentApi.getStudents().size
        return "STU-$year-${String.format("%04d", count + 1)}"
    }

    // --- Dummy Guardian functions since API doesn't have Guardians yet ---
    suspend fun insertGuardian(guardian: Guardian): Long = 1L
    suspend fun updateGuardian(guardian: Guardian) {}
    suspend fun deleteGuardian(guardian: Guardian) {}
    fun getGuardiansByStudent(studentId: Long): Flow<List<Guardian>> = flow { emit(emptyList()) }
    suspend fun deleteGuardiansByStudent(studentId: Long) {}

    // --- Courses & Batches ---
    val activeCourses: Flow<List<Course>> = flow {
        emit(ApiClient.courseApi.getCourses().filter { it.isActive })
    }
    
    val activeBatches: Flow<List<Batch>> = flow {
        emit(ApiClient.courseApi.getBatches().filter { it.isActive })
    }

    suspend fun getCourseById(id: Long): Course? {
        return ApiClient.courseApi.getCourses().find { it.id == id }
    }
    
    fun getBatchesByCourse(courseId: Long): Flow<List<Batch>> = flow {
        emit(ApiClient.courseApi.getBatches().filter { it.courseId == courseId })
    }
}
