package com.ims.app.data.dao

import androidx.room.*
import com.ims.app.data.model.News
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for News entity.
 * Manages institute announcements and news items.
 */
@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: News): Long

    @Update
    suspend fun updateNews(news: News)

    @Delete
    suspend fun deleteNews(news: News)

    @Query("SELECT * FROM news WHERE isPublished = 1 ORDER BY publishedAt DESC")
    fun getPublishedNews(): Flow<List<News>>

    @Query("SELECT * FROM news ORDER BY createdAt DESC")
    fun getAllNews(): Flow<List<News>>

    @Query("SELECT * FROM news WHERE isPublished = 1 ORDER BY publishedAt DESC LIMIT :limit")
    fun getLatestNews(limit: Int = 5): Flow<List<News>>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getNewsById(id: Long): News?

    @Query("""
        SELECT * FROM news 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%'
        ORDER BY publishedAt DESC
    """)
    fun searchNews(query: String): Flow<List<News>>
}
