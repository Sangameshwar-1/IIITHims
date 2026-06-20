package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a fee collection record for a student.
 * Tracks payment status, due dates, and transaction details.
 */
@Entity(tableName = "fee_records")
data class FeeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val feeType: String,            // Tuition, Hostel, Library, Exam, Transport
    val amount: Double,
    val dueDate: String,            // ISO format
    val paidDate: String = "",
    val paidAmount: Double = 0.0,
    val paymentMode: String = "",   // Cash, Online, DD, Cheque
    val transactionId: String = "",
    val status: String = "Pending", // Pending, Paid, Partial, Overdue, Waived
    val remarks: String = "",
    val semester: Int = 1,
    val academicYear: String = "2024-25",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents an institute expense record.
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,           // Salaries, Infrastructure, Lab, Events, etc.
    val description: String,
    val amount: Double,
    val date: String,               // ISO format
    val approvedBy: String = "",
    val receiptNo: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
