package com.ims.app.data.dao

import androidx.room.*
import com.ims.app.data.model.Batch
import com.ims.app.data.model.Course
import com.ims.app.data.model.Subject
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Course, Batch, and Subject entities.
 */
@Dao
interface CourseDao {

    // ─── Course ─────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM courses WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses ORDER BY name ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Long): Course?

    @Query("SELECT COUNT(*) FROM courses WHERE isActive = 1")
    fun getActiveCourseCount(): Flow<Int>

    // ─── Batch ──────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: Batch): Long

    @Update
    suspend fun updateBatch(batch: Batch)

    @Query("SELECT * FROM batches WHERE courseId = :courseId AND isActive = 1 ORDER BY startYear DESC")
    fun getBatchesByCourse(courseId: Long): Flow<List<Batch>>

    @Query("SELECT * FROM batches WHERE isActive = 1 ORDER BY startYear DESC")
    fun getAllActiveBatches(): Flow<List<Batch>>

    @Query("SELECT * FROM batches WHERE id = :id")
    suspend fun getBatchById(id: Long): Batch?

    // ─── Subject ────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("SELECT * FROM subjects WHERE courseId = :courseId AND isActive = 1 ORDER BY semester, name")
    fun getSubjectsByCourse(courseId: Long): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): Subject?
}
