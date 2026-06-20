package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a guardian/parent of a student.
 * Linked to a student via studentId for emergency contacts and communications.
 */
@Entity(tableName = "guardians")
data class Guardian(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val name: String,
    val relation: String,       // Father, Mother, Guardian, etc.
    val phone: String,
    val email: String = "",
    val occupation: String = "",
    val address: String = "",
    val isEmergencyContact: Boolean = false
)
