package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a timetable slot for a batch.
 * Stores day, period, subject, and teacher assignment for scheduling.
 */
@Entity(tableName = "timetable")
data class TimetableSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val batchId: Long,
    val subjectId: Long,
    val dayOfWeek: Int,             // 1=Mon, 2=Tue, ... 7=Sun
    val periodNumber: Int,          // 1-8
    val startTime: String,          // HH:mm
    val endTime: String,            // HH:mm
    val room: String = "",
    val teacherName: String = "",
    val isActive: Boolean = true
)
