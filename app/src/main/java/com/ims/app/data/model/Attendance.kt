package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an attendance record for a student in a subject session.
 * Linked to a student, subject, and batch for daily/monthly reports.
 */
@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: Long,
    val batchId: Long,
    val date: String,               // ISO format yyyy-MM-dd
    val status: String,             // Present, Absent, Late, Excused
    val remarks: String = "",
    val markedBy: String = "Admin", // Teacher/Admin who marked attendance
    val markedAt: Long = System.currentTimeMillis()
)
