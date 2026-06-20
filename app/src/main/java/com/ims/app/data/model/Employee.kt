package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an employee (faculty or staff) in the institute.
 * Covers full employee lifecycle from admission to exit.
 */
@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: String,             // Auto-generated: EMP-YEAR-XXXX
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val department: String,
    val designation: String,            // Professor, Asst. Professor, HOD, etc.
    val employeeType: String = "Faculty", // Faculty, Staff, Contractual
    val dateOfBirth: String = "",
    val dateOfJoining: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val qualification: String = "",     // PhD, M.Tech, etc.
    val specialization: String = "",
    val baseSalary: Double = 0.0,
    val status: String = "Active",      // Active, On Leave, Resigned, Retired
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a salary slip (payslip) for an employee.
 * Supports one-click approval/rejection workflow.
 */
@Entity(tableName = "payslips")
data class Payslip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val month: String,                  // e.g., "2025-03"
    val basicSalary: Double,
    val hra: Double = 0.0,              // House Rent Allowance
    val da: Double = 0.0,              // Dearness Allowance
    val ta: Double = 0.0,              // Travel Allowance
    val otherAllowances: Double = 0.0,
    val pf: Double = 0.0,              // Provident Fund deduction
    val tax: Double = 0.0,             // TDS deduction
    val otherDeductions: Double = 0.0,
    val netSalary: Double = basicSalary + hra + da + ta + otherAllowances - pf - tax - otherDeductions,
    val status: String = "Pending",    // Pending, Approved, Rejected
    val approvedBy: String = "",
    val approvedAt: Long = 0,
    val generatedAt: Long = System.currentTimeMillis()
)
