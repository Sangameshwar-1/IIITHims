package com.ims.app.data.dao

import androidx.room.*
import com.ims.app.data.model.Guardian
import com.ims.app.data.model.Student
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Student and Guardian entities.
 * Provides CRUD operations and advanced search queries.
 */
@Dao
interface StudentDao {

    // ─── Student CRUD ───────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students ORDER BY createdAt DESC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: String): Student?

    @Query("SELECT * FROM students WHERE status = :status ORDER BY createdAt DESC")
    fun getStudentsByStatus(status: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE batchId = :batchId ORDER BY firstName ASC")
    fun getStudentsByBatch(batchId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE courseId = :courseId ORDER BY firstName ASC")
    fun getStudentsByCourse(courseId: Long): Flow<List<Student>>

    @Query("""
        SELECT * FROM students 
        WHERE firstName LIKE '%' || :query || '%' 
        OR lastName LIKE '%' || :query || '%'
        OR studentId LIKE '%' || :query || '%'
        OR email LIKE '%' || :query || '%'
        ORDER BY firstName ASC
    """)
    fun searchStudents(query: String): Flow<List<Student>>

    @Query("""
        SELECT * FROM students 
        WHERE (:status = '' OR status = :status)
        AND (:courseId = 0 OR courseId = :courseId)
        AND (:batchId = 0 OR batchId = :batchId)
        AND (:category = '' OR category = :category)
        AND (:gender = '' OR gender = :gender)
        ORDER BY firstName ASC
    """)
    fun advancedSearch(
        status: String = "",
        courseId: Long = 0,
        batchId: Long = 0,
        category: String = "",
        gender: String = ""
    ): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getTotalStudentCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students WHERE status = 'Active'")
    fun getActiveStudentCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students WHERE status = 'Active' AND admissionDate >= :since")
    fun getRecentAdmissionCount(since: String): Flow<Int>

    // ─── Guardian CRUD ──────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardian(guardian: Guardian): Long

    @Update
    suspend fun updateGuardian(guardian: Guardian)

    @Delete
    suspend fun deleteGuardian(guardian: Guardian)

    @Query("SELECT * FROM guardians WHERE studentId = :studentId")
    fun getGuardiansByStudent(studentId: Long): Flow<List<Guardian>>

    @Query("DELETE FROM guardians WHERE studentId = :studentId")
    suspend fun deleteGuardiansByStudent(studentId: Long)
}
