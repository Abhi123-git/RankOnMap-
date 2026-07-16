package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDao {

    // --- Business Profile Queries ---
    @Query("SELECT * FROM business_profile WHERE id = 1")
    fun getBusinessProfileFlow(): Flow<BusinessProfile?>

    @Query("SELECT * FROM business_profile WHERE id = 1")
    suspend fun getBusinessProfileDirect(): BusinessProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessProfile(profile: BusinessProfile)

    // --- Keyword Ranks Queries ---
    @Query("SELECT * FROM keyword_ranks ORDER BY keyword ASC")
    fun getAllKeywordsFlow(): Flow<List<KeywordRank>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: KeywordRank)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywords(keywords: List<KeywordRank>)

    @Query("DELETE FROM keyword_ranks WHERE id = :id")
    suspend fun deleteKeyword(id: Int)

    @Query("DELETE FROM keyword_ranks")
    suspend fun clearAllKeywords()

    // --- GeoGrid Points Queries ---
    @Query("SELECT * FROM geogrid_points WHERE keyword = :keyword ORDER BY gridY ASC, gridX ASC")
    fun getPointsForKeywordFlow(keyword: String): Flow<List<GeoGridPoint>>

    @Query("SELECT * FROM geogrid_points ORDER BY timestamp DESC")
    fun getAllGeoGridPointsFlow(): Flow<List<GeoGridPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeoGridPoints(points: List<GeoGridPoint>)

    @Query("DELETE FROM geogrid_points WHERE keyword = :keyword")
    suspend fun clearGeoGridPoints(keyword: String)

    // --- Review Items Queries ---
    @Query("SELECT * FROM review_items ORDER BY reviewDate DESC")
    fun getAllReviewsFlow(): Flow<List<ReviewItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewItem>)

    @Query("UPDATE review_items SET aiReply = :reply, replyStatus = :status WHERE id = :reviewId")
    suspend fun updateReviewReply(reviewId: String, reply: String, status: String)

    // --- SEO Audits Queries ---
    @Query("SELECT * FROM seo_audits ORDER BY timestamp DESC LIMIT 1")
    fun getLatestAuditFlow(): Flow<SeoAudit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeoAudit(audit: SeoAudit)
}
