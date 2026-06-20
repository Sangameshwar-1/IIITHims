package com.ims.app.data.dao

import androidx.room.*
import com.ims.app.data.model.Attendance
import kotlinx.coroutines.flow.Flow

/**
 * DAO for attendance record operations.
 */
@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<Attendance>)

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE subjectId = :subjectId AND date = :date ORDER BY studentId ASC")
    fun getAttendanceBySubjectAndDate(subjectId: Long, date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE batchId = :batchId AND date BETWEEN :fromDate AND :toDate")
    fun getAttendanceByBatchAndRange(batchId: Long, fromDate: String, toDate: String): Flow<List<Attendance>>

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND subjectId = :subjectId AND status = 'Present'")
    suspend fun getPresentCount(studentId: Long, subjectId: Long): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND subjectId = :subjectId")
    suspend fun getTotalCount(studentId: Long, subjectId: Long): Int

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date = :date")
    suspend fun getAttendanceByStudentAndDate(studentId: Long, date: String): Attendance?
}
