package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an examination event in the system.
 * Exams can be grade-based, marks-based, or custom types.
 */
@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,               // e.g., "Mid-Semester Exam 2024"
    val type: String,               // Marks, Grade, GPA, CCE, CWA
    val subjectId: Long,
    val courseId: Long,
    val batchId: Long,
    val date: String,               // ISO format
    val startTime: String,          // HH:mm
    val endTime: String,            // HH:mm
    val totalMarks: Int = 100,
    val passingMarks: Int = 40,
    val description: String = "",
    val venue: String = "",
    val status: String = "Scheduled", // Scheduled, Ongoing, Completed, Cancelled
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a student's result for a specific exam.
 */
@Entity(tableName = "exam_results")
data class ExamResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val studentId: Long,
    val marksObtained: Double = 0.0,
    val grade: String = "",          // A+, A, B+, B, etc.
    val gpa: Double = 0.0,
    val remarks: String = "",
    val status: String = "Pending",  // Pending, Passed, Failed, Absent
    val evaluatedBy: String = "",
    val evaluatedAt: Long = 0
)

/**
 * Aggregated exam statistics for reporting.
 */
data class ExamStats(
    val totalExams: Int = 0,
    val completedExams: Int = 0,
    val averageScore: Double = 0.0,
    val passPercentage: Double = 0.0,
    val highestScore: Double = 0.0,
    val lowestScore: Double = 0.0
)
