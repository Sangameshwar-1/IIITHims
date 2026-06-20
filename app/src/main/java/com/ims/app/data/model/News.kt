package com.ims.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a news/announcement item published in the system.
 * Displayed on the dashboard and accessible via the news section.
 */
@Entity(tableName = "news")
data class News(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val author: String,
    val category: String = "General",   // General, Academic, Event, Holiday
    val isPublished: Boolean = true,
    val publishedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a user in the system with role-based access control.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val displayName: String,
    val email: String,
    val role: String,                   // Admin, Teacher, Student, Parent
    val avatarUrl: String = "",
    val isActive: Boolean = true,
    val lastLogin: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
