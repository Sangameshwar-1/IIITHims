package com.ims.app.data.dao

import androidx.room.*
import com.ims.app.data.model.Exam
import com.ims.app.data.model.ExamResult
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Exam and ExamResult entities.
 * Supports exam management, result recording, and statistical queries.
 */
@Dao
interface ExamDao {

    // ─── Exam CRUD ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("SELECT * FROM exams ORDER BY date DESC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: Long): Exam?

    @Query("SELECT * FROM exams WHERE status = :status ORDER BY date ASC")
    fun getExamsByStatus(status: String): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE courseId = :courseId ORDER BY date DESC")
    fun getExamsByCourse(courseId: Long): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE batchId = :batchId ORDER BY date DESC")
    fun getExamsByBatch(batchId: Long): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE subjectId = :subjectId ORDER BY date DESC")
    fun getExamsBySubject(subjectId: Long): Flow<List<Exam>>

    @Query("""
        SELECT * FROM exams 
        WHERE name LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY date DESC
    """)
    fun searchExams(query: String): Flow<List<Exam>>

    @Query("SELECT COUNT(*) FROM exams")
    fun getTotalExamCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM exams WHERE status = 'Scheduled'")
    fun getUpcomingExamCount(): Flow<Int>

    @Query("SELECT * FROM exams WHERE status = 'Scheduled' ORDER BY date ASC LIMIT 5")
    fun getUpcomingExams(): Flow<List<Exam>>

    // ─── ExamResult CRUD ────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ExamResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<ExamResult>)

    @Update
    suspend fun updateResult(result: ExamResult)

    @Delete
    suspend fun deleteResult(result: ExamResult)

    @Query("SELECT * FROM exam_results WHERE examId = :examId ORDER BY marksObtained DESC")
    fun getResultsByExam(examId: Long): Flow<List<ExamResult>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY evaluatedAt DESC")
    fun getResultsByStudent(studentId: Long): Flow<List<ExamResult>>

    @Query("SELECT * FROM exam_results WHERE examId = :examId AND studentId = :studentId")
    suspend fun getResult(examId: Long, studentId: Long): ExamResult?

    // ─── Statistics ─────────────────────────────────────────────

    @Query("SELECT AVG(marksObtained) FROM exam_results WHERE examId = :examId AND status != 'Absent'")
    suspend fun getAverageMarks(examId: Long): Double?

    @Query("SELECT MAX(marksObtained) FROM exam_results WHERE examId = :examId")
    suspend fun getHighestMarks(examId: Long): Double?

    @Query("SELECT MIN(marksObtained) FROM exam_results WHERE examId = :examId AND status != 'Absent'")
    suspend fun getLowestMarks(examId: Long): Double?

    @Query("SELECT COUNT(*) FROM exam_results WHERE examId = :examId AND status = 'Passed'")
    suspend fun getPassCount(examId: Long): Int

    @Query("SELECT COUNT(*) FROM exam_results WHERE examId = :examId AND status != 'Absent'")
    suspend fun getTotalAttempted(examId: Long): Int
}
