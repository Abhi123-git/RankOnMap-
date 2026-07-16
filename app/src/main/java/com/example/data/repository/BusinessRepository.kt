package com.example.data.repository

import com.example.data.local.LocalDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BusinessRepository(private val dao: LocalDao) {

    val businessProfile: Flow<BusinessProfile?> = dao.getBusinessProfileFlow()
    val allKeywords: Flow<List<KeywordRank>> = dao.getAllKeywordsFlow()
    val allReviews: Flow<List<ReviewItem>> = dao.getAllReviewsFlow()
    val latestAudit: Flow<SeoAudit?> = dao.getLatestAuditFlow()
    val allGeoGridPoints: Flow<List<GeoGridPoint>> = dao.getAllGeoGridPointsFlow()

    fun getPointsForKeyword(keyword: String): Flow<List<GeoGridPoint>> = dao.getPointsForKeywordFlow(keyword)

    suspend fun saveBusinessProfile(profile: BusinessProfile) {
        dao.insertBusinessProfile(profile)
    }

    suspend fun addKeyword(keywordText: String) {
        // Start new keywords at position 15
        dao.insertKeyword(
            KeywordRank(
                keyword = keywordText,
                currentRank = 15,
                previousRank = 15,
                searchVolume = (200..1500).random()
            )
        )
    }

    suspend fun deleteKeyword(id: Int) {
        dao.deleteKeyword(id)
    }

    suspend fun saveGeoGridPoints(points: List<GeoGridPoint>) {
        dao.insertGeoGridPoints(points)
    }

    suspend fun updateReviewReply(reviewId: String, reply: String, status: String) {
        dao.updateReviewReply(reviewId, reply, status)
    }

    suspend fun saveSeoAudit(audit: SeoAudit) {
        dao.insertSeoAudit(audit)
    }

    suspend fun getBusinessProfileDirect(): BusinessProfile? {
        return dao.getBusinessProfileDirect()
    }

    suspend fun generateGeoGridForKeyword(keyword: String, centerRank: Int) {
        val points = mutableListOf<GeoGridPoint>()
        // Generate a 3x3 grid around centerRank
        for (y in 0 until 3) {
            for (x in 0 until 3) {
                // Ranks are usually better closer to the center (1,1)
                val distanceFromCenter = kotlin.math.abs(x - 1) + kotlin.math.abs(y - 1)
                val pointRank = if (centerRank == -1) {
                    -1
                } else {
                    val variance = (-1..2).random()
                    val calculated = centerRank + (distanceFromCenter * 2) + variance
                    calculated.coerceIn(1, 20)
                }
                
                val competitors = listOf("Bean & Co", "Brewed Awakenings", "Starbucks", "Philz Coffee", "Blue Bottle", "Peet's")
                val competitor = if (pointRank > 3) competitors.random() else null

                points.add(
                    GeoGridPoint(
                        keyword = keyword,
                        gridX = x,
                        gridY = y,
                        rank = pointRank,
                        competitorName = competitor
                    )
                )
            }
        }
        dao.clearGeoGridPoints(keyword)
        dao.insertGeoGridPoints(points)
    }

    suspend fun resetToDefault() {
        dao.clearAllKeywords()
        
        val defaultProfile = BusinessProfile(
            name = "The Coffee Grind",
            category = "Specialty Coffee Shop",
            address = "525 Market St, San Francisco, CA 94105",
            targetLocation = "Financial District, SF"
        )
        dao.insertBusinessProfile(defaultProfile)

        val defaultKeywords = listOf(
            KeywordRank(keyword = "specialty coffee near me", currentRank = 3, previousRank = 4, searchVolume = 1200),
            KeywordRank(keyword = "best espresso financial district", currentRank = 7, previousRank = 5, searchVolume = 850),
            KeywordRank(keyword = "craft latte sf", currentRank = 12, previousRank = 15, searchVolume = 620),
            KeywordRank(keyword = "wifi workspace cafe", currentRank = 19, previousRank = 22, searchVolume = 1100),
            KeywordRank(keyword = "organic cold brew near me", currentRank = 1, previousRank = 1, searchVolume = 340)
        )
        dao.insertKeywords(defaultKeywords)

        for (k in defaultKeywords) {
            generateGeoGridForKeyword(k.keyword, k.currentRank)
        }

        val defaultReviews = listOf(
            ReviewItem(
                id = "rev_1",
                authorName = "Emily Watson",
                rating = 5,
                comment = "Absolutely loved the atmosphere here! The single-origin pour over was phenomenal and the barista was super friendly. My new daily spot in SF.",
                reviewDate = "2 hours ago"
            ),
            ReviewItem(
                id = "rev_2",
                authorName = "Marcus Vance",
                rating = 3,
                comment = "Decent flat white, but the Wi-Fi was really slow and it was hard to find a table with a power outlet. Pastry was nice and flaky though.",
                reviewDate = "1 day ago"
            ),
            ReviewItem(
                id = "rev_3",
                authorName = "Sasha G.",
                rating = 4,
                comment = "Great lattes, and their oat milk brand is highly premium. Service can be a bit slow during the morning rush, but it's worth the wait.",
                reviewDate = "3 days ago"
            ),
            ReviewItem(
                id = "rev_4",
                authorName = "David Kim",
                rating = 2,
                comment = "I ordered a double espresso but it was extremely sour and under-extracted. The place is decorated nicely, but they need to calibrate their grinder.",
                reviewDate = "1 week ago"
            )
        )
        dao.insertReviews(defaultReviews)

        val defaultAuditJson = """
            {
              "critical_issues": [
                "Missing business hours on holidays",
                "Google Business Profile description lacks core keywords like 'Financial District espresso' or 'SF craft coffee'",
                "Average response time for 1-3 star reviews is greater than 14 days"
              ],
              "opportunities": [
                "Add 5-10 high-resolution photos of your signature coffee and pastry pairings to stand out",
                "Create a weekly Google Business post offering localized mid-week promotions to boost relevance",
                "Acquire 3 new local citations in regional business registers (e.g. SF Chamber of Commerce)"
              ],
              "citation_health": "72/100 (Consistent NAP on 12/18 main directories)"
            }
        """.trimIndent()

        val defaultAudit = SeoAudit(
            businessName = "The Coffee Grind",
            auditScore = 74,
            gmbCompleteness = 80,
            citationCount = 12,
            recommendationsJson = defaultAuditJson
        )
        dao.insertSeoAudit(defaultAudit)
    }

    suspend fun prepopulateIfEmpty() {
        val currentProfile = dao.getBusinessProfileDirect()
        if (currentProfile == null) {
            resetToDefault()
        }
    }
}
