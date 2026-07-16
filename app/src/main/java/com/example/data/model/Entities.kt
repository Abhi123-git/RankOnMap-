package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val category: String,
    val address: String,
    val targetLocation: String,
    val latitude: Double = 37.7749,
    val longitude: Double = -122.4194,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "keyword_ranks")
data class KeywordRank(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val keyword: String,
    val currentRank: Int, // 1 to 20+, -1 if unranked
    val previousRank: Int,
    val searchVolume: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "geogrid_points")
data class GeoGridPoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val keyword: String,
    val gridX: Int, // 0 to 4
    val gridY: Int, // 0 to 4
    val rank: Int,  // ranking number (1-20, or 99 if unranked)
    val competitorName: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "review_items")
data class ReviewItem(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val rating: Int,
    val comment: String,
    val reviewDate: String,
    val aiReply: String? = null,
    val replyStatus: String = "None" // "None", "Draft", "Published"
)

@Entity(tableName = "seo_audits")
data class SeoAudit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val businessName: String,
    val auditScore: Int,
    val gmbCompleteness: Int,
    val citationCount: Int,
    val recommendationsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
