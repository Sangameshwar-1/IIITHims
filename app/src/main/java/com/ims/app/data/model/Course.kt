package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an academic course offered by the institute.
 */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,               // e.g., "CS101"
    val department: String,
    val duration: String,            // e.g., "4 Years"
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a batch within a course (e.g., "2024-2028 CSE Batch A").
 */
@Entity(tableName = "batches")
data class Batch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val courseId: Long,
    val startYear: Int,
    val endYear: Int,
    val maxStrength: Int = 60,
    val currentStrength: Int = 0,
    val isActive: Boolean = true
)

/**
 * Represents a subject within a course.
 */
@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,               // e.g., "CS201"
    val courseId: Long,
    val creditHours: Int = 3,
    val type: String = "Core",      // Core, Elective
    val semester: Int = 1,
    val isActive: Boolean = true
)
