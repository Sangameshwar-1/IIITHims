package com.ims.app.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions used across the IMS application.
 */
object Utils {

    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    /**
     * Converts ISO date string (yyyy-MM-dd) to a human-readable format (dd MMM yyyy).
     * Returns the original string if parsing fails.
     */
    fun formatDate(isoDate: String): String {
        return try {
            val date = isoFormat.parse(isoDate)
            if (date != null) displayFormat.format(date) else isoDate
        } catch (e: Exception) {
            isoDate
        }
    }

    /**
     * Returns today's date as an ISO string (yyyy-MM-dd).
     */
    fun todayIso(): String = isoFormat.format(Date())

    /**
     * Converts HH:mm time string to 12-hour display format (hh:mm AM/PM).
     */
    fun formatTime(time24: String): String {
        return try {
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf24.parse(time24)
            if (date != null) timeFormat.format(date) else time24
        } catch (e: Exception) {
            time24
        }
    }

    /**
     * Returns month abbreviation from date string.
     */
    fun monthAbbr(isoDate: String): String {
        val months = mapOf(
            "01" to "Jan", "02" to "Feb", "03" to "Mar", "04" to "Apr",
            "05" to "May", "06" to "Jun", "07" to "Jul", "08" to "Aug",
            "09" to "Sep", "10" to "Oct", "11" to "Nov", "12" to "Dec"
        )
        val parts = isoDate.split("-")
        return months[parts.getOrElse(1) { "01" }] ?: "???"
    }

    /**
     * Returns day of month from ISO date string.
     */
    fun dayOfMonth(isoDate: String): String = isoDate.split("-").getOrElse(2) { "??" }

    /**
     * Generates initials from a full name (up to 2 characters).
     */
    fun initials(firstName: String, lastName: String = ""): String {
        val f = firstName.firstOrNull()?.uppercaseChar() ?: ""
        val l = lastName.firstOrNull()?.uppercaseChar() ?: ""
        return "$f$l"
    }

    /**
     * Calculates letter grade from percentage.
     */
    fun gradeFromPercentage(percentage: Double): String = when {
        percentage >= 90 -> "A+"
        percentage >= 80 -> "A"
        percentage >= 70 -> "B+"
        percentage >= 60 -> "B"
        percentage >= 50 -> "C+"
        percentage >= 40 -> "C"
        percentage >= 35 -> "D"
        else -> "F"
    }

    /**
     * Calculates GPA (10-point scale) from percentage.
     */
    fun gpaFromPercentage(percentage: Double): Double = when {
        percentage >= 90 -> 10.0
        percentage >= 80 -> 9.0
        percentage >= 70 -> 8.0
        percentage >= 60 -> 7.0
        percentage >= 50 -> 6.0
        percentage >= 40 -> 5.0
        else -> 0.0
    }

    /**
     * Validates an email address format.
     */
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * Validates a 10-digit Indian phone number.
     */
    fun isValidPhone(phone: String): Boolean =
        phone.length == 10 && phone.all { it.isDigit() }

    /**
     * Formats a number with commas for Indian number system.
     * e.g., 1234567 -> 12,34,567
     */
    fun formatIndianNumber(number: Int): String {
        val s = number.toString()
        if (s.length <= 3) return s
        val last3 = s.takeLast(3)
        val rest = s.dropLast(3)
        val formatted = rest.reversed().chunked(2).joinToString(",").reversed()
        return "$formatted,$last3"
    }
}
