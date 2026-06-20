package com.ims.app.util

/**
 * App-wide constants used across modules.
 */
object Constants {

    // ─── Student Status ─────────────────────────────────────────
    const val STATUS_ACTIVE = "Active"
    const val STATUS_GRADUATED = "Graduated"
    const val STATUS_DROPPED = "Dropped"
    const val STATUS_SUSPENDED = "Suspended"

    val STUDENT_STATUSES = listOf(STATUS_ACTIVE, STATUS_GRADUATED, STATUS_DROPPED, STATUS_SUSPENDED)

    // ─── Exam Status ─────────────────────────────────────────────
    const val EXAM_SCHEDULED = "Scheduled"
    const val EXAM_ONGOING = "Ongoing"
    const val EXAM_COMPLETED = "Completed"
    const val EXAM_CANCELLED = "Cancelled"

    val EXAM_STATUSES = listOf(EXAM_SCHEDULED, EXAM_ONGOING, EXAM_COMPLETED, EXAM_CANCELLED)

    // ─── Exam Types ──────────────────────────────────────────────
    const val EXAM_TYPE_MARKS = "Marks"
    const val EXAM_TYPE_GRADE = "Grade"
    const val EXAM_TYPE_GPA = "GPA"
    const val EXAM_TYPE_CCE = "CCE"
    const val EXAM_TYPE_CWA = "CWA"

    val EXAM_TYPES = listOf(EXAM_TYPE_MARKS, EXAM_TYPE_GRADE, EXAM_TYPE_GPA, EXAM_TYPE_CCE, EXAM_TYPE_CWA)

    // ─── Result Status ───────────────────────────────────────────
    const val RESULT_PASSED = "Passed"
    const val RESULT_FAILED = "Failed"
    const val RESULT_ABSENT = "Absent"
    const val RESULT_PENDING = "Pending"

    // ─── Gender ─────────────────────────────────────────────────
    val GENDERS = listOf("Male", "Female", "Other")

    // ─── Blood Groups ────────────────────────────────────────────
    val BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // ─── Student Categories ──────────────────────────────────────
    val STUDENT_CATEGORIES = listOf("General", "OBC", "SC", "ST", "EWS")

    // ─── Guardian Relations ──────────────────────────────────────
    val GUARDIAN_RELATIONS = listOf("Father", "Mother", "Guardian", "Sibling", "Spouse", "Other")

    // ─── Subject Types ───────────────────────────────────────────
    val SUBJECT_TYPES = listOf("Core", "Elective", "Lab", "Project")

    // ─── News Categories ─────────────────────────────────────────
    val NEWS_CATEGORIES = listOf("General", "Academic", "Event", "Holiday")

    // ─── Grading Scales ──────────────────────────────────────────
    val GRADES = listOf("A+", "A", "B+", "B", "C+", "C", "D", "F")

    // ─── User Roles ──────────────────────────────────────────────
    const val ROLE_ADMIN = "Admin"
    const val ROLE_TEACHER = "Teacher"
    const val ROLE_STUDENT = "Student"
    const val ROLE_PARENT = "Parent"

    val USER_ROLES = listOf(ROLE_ADMIN, ROLE_TEACHER, ROLE_STUDENT, ROLE_PARENT)

    // ─── Student ID format ───────────────────────────────────────
    const val STUDENT_ID_PREFIX = "STU"

    // ─── Pagination ──────────────────────────────────────────────
    const val PAGE_SIZE = 20
    const val RECENT_ITEMS_LIMIT = 5
    const val NEWS_PREVIEW_LIMIT = 5

    // ─── Tracking ────────────────────────────────────────────────
    const val TRACKER_URL = "https://project-tracker-0eju.onrender.com/api/data"
}
