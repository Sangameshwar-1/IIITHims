package com.ims.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ims.app.data.dao.CourseDao
import com.ims.app.data.dao.ExamDao
import com.ims.app.data.dao.NewsDao
import com.ims.app.data.dao.StudentDao
import com.ims.app.data.model.*

/**
 * Central Room database for the Institute Management System.
 * Uses singleton pattern to ensure a single database instance.
 */
@Database(
    entities = [
        Student::class,
        Guardian::class,
        Course::class,
        Batch::class,
        Subject::class,
        Exam::class,
        ExamResult::class,
        News::class,
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ImsDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun examDao(): ExamDao
    abstract fun courseDao(): CourseDao
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: ImsDatabase? = null

        /**
         * Returns the singleton database instance, creating it if necessary.
         * Uses double-checked locking for thread safety.
         */
        fun getDatabase(context: Context): ImsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImsDatabase::class.java,
                    "ims_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
