package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a student in the Institute Management System.
 * Stores personal details, enrollment information, and academic status.
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,          // Unique institute ID (auto-generated)
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,        // ISO format: yyyy-MM-dd
    val gender: String,             // Male, Female, Other
    val address: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val bloodGroup: String,
    val nationality: String = "Indian",
    val religion: String = "",
    val category: String = "General", // General, OBC, SC, ST, etc.
    val photoUrl: String = "",
    val batchId: Long = 0,
    val courseId: Long = 0,
    val admissionDate: String,      // ISO format
    val previousSchool: String = "",
    val previousGrade: String = "",
    val previousPercentage: String = "",
    val status: String = "Active",  // Active, Graduated, Dropped, Suspended
    val createdAt: Long = System.currentTimeMillis()
)
