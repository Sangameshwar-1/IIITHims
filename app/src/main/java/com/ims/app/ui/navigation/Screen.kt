package com.ims.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class defining all navigation routes in the app.
 * Each route has a unique identifier and display metadata.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // ─── Bottom Navigation Destinations ────────────────────────
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Admissions : Screen("admissions", "Admissions", Icons.Filled.PersonAdd)
    object Examinations : Screen("examinations", "Exams", Icons.Filled.Assignment)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    // ─── Authentication ────────────────────────────────────────
    object Login : Screen("login", "Login")

    // ─── Admission Sub-screens ─────────────────────────────────
    object AdmissionForm : Screen("admission_form?studentId={studentId}", "New Admission") {
        fun createRoute(studentId: Long = -1L) = "admission_form?studentId=$studentId"
    }
    object StudentDetail : Screen("student_detail/{studentId}", "Student Details") {
        fun createRoute(studentId: Long) = "student_detail/$studentId"
    }

    // ─── Examination Sub-screens ───────────────────────────────
    object CreateExam : Screen("create_exam?examId={examId}", "Create Exam") {
        fun createRoute(examId: Long = -1L) = "create_exam?examId=$examId"
    }
    object ExamResults : Screen("exam_results/{examId}", "Exam Results") {
        fun createRoute(examId: Long) = "exam_results/$examId"
    }
    object ExamReports : Screen("exam_reports", "Reports")

    // ─── Bottom nav items ──────────────────────────────────────
    companion object {
        val bottomNavItems = listOf(Dashboard, Admissions, Examinations, Settings)
    }
}
