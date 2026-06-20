package com.ims.app.data.dao

import androidx.room.*
import com.ims.app.data.model.Employee
import com.ims.app.data.model.Payslip
import kotlinx.coroutines.flow.Flow

/**
 * DAO for employee and payroll operations.
 */
@Dao
interface EmployeeDao {

    // ─── Employee CRUD ───────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Query("SELECT * FROM employees ORDER BY firstName ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE status = 'Active' ORDER BY firstName ASC")
    fun getActiveEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Long): Employee?

    @Query("SELECT * FROM employees WHERE department = :dept ORDER BY firstName ASC")
    fun getEmployeesByDepartment(dept: String): Flow<List<Employee>>

    @Query("""
        SELECT * FROM employees WHERE 
        firstName LIKE '%' || :query || '%' OR 
        lastName LIKE '%' || :query || '%' OR
        employeeId LIKE '%' || :query || '%' OR
        department LIKE '%' || :query || '%'
        ORDER BY firstName ASC
    """)
    fun searchEmployees(query: String): Flow<List<Employee>>

    @Query("SELECT COUNT(*) FROM employees WHERE status = 'Active'")
    fun getActiveCount(): Flow<Int>

    // ─── Payslip CRUD ────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayslip(payslip: Payslip): Long

    @Update
    suspend fun updatePayslip(payslip: Payslip)

    @Query("SELECT * FROM payslips WHERE employeeId = :employeeId ORDER BY month DESC")
    fun getPayslipsByEmployee(employeeId: Long): Flow<List<Payslip>>

    @Query("SELECT * FROM payslips WHERE month = :month ORDER BY employeeId ASC")
    fun getPayslipsByMonth(month: String): Flow<List<Payslip>>

    @Query("SELECT * FROM payslips WHERE status = 'Pending' ORDER BY month DESC")
    fun getPendingPayslips(): Flow<List<Payslip>>

    @Query("SELECT SUM(netSalary) FROM payslips WHERE month = :month AND status = 'Approved'")
    suspend fun getTotalPayrollForMonth(month: String): Double?
}
