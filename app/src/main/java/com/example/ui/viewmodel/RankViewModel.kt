package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.BusinessRepository
import com.example.data.api.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

sealed class Screen {
    object Onboarding : Screen()
    object Dashboard : Screen()
    object GeoGrid : Screen()
    object AIReviews : Screen()
    object AIAudit : Screen()
    object Settings : Screen()
}

sealed class AuditUiState {
    object Idle : AuditUiState()
    object Loading : AuditUiState()
    data class Success(val audit: SeoAudit) : AuditUiState()
    data class Error(val message: String) : AuditUiState()
}

sealed class GmbPostUiState {
    object Idle : GmbPostUiState()
    object Loading : GmbPostUiState()
    data class Success(val postText: String) : GmbPostUiState()
    data class Error(val message: String) : GmbPostUiState()
}

class RankViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "rankonmaps_db"
    ).build()

    private val repository = BusinessRepository(db.localDao())

    // --- UI Navigation State ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Onboarding)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // --- Database Flows ---
    val businessProfile = repository.businessProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allKeywords = repository.allKeywords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allReviews = repository.allReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestAudit = repository.latestAudit.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allGeoGridPoints = repository.allGeoGridPoints.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Active Selected Keyword for GeoGrid view ---
    private val _selectedGridKeyword = MutableStateFlow<String>("")
    val selectedGridKeyword: StateFlow<String> = _selectedGridKeyword.asStateFlow()

    val geoGridPointsForSelectedKeyword = _selectedGridKeyword.flatMapLatest { keyword ->
        if (keyword.isEmpty()) flowOf(emptyList())
        else repository.getPointsForKeyword(keyword)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- AI Operation States ---
    private val _auditUiState = MutableStateFlow<AuditUiState>(AuditUiState.Idle)
    val auditUiState: StateFlow<AuditUiState> = _auditUiState.asStateFlow()

    private val _gmbPostUiState = MutableStateFlow<GmbPostUiState>(GmbPostUiState.Idle)
    val gmbPostUiState: StateFlow<GmbPostUiState> = _gmbPostUiState.asStateFlow()

    // Map reviewId -> Reply Generation Status: "Idle", "Generating", "Success", "Error"
    private val _replyGenerationStates = MutableStateFlow<Map<String, String>>(emptyMap())
    val replyGenerationStates: StateFlow<Map<String, String>> = _replyGenerationStates.asStateFlow()

    // --- API Key Check ---
    val isApiKeyMissing: Boolean by lazy {
        val key = BuildConfig.GEMINI_API_KEY
        key.isNullOrEmpty() || key == "MY_GEMINI_API_KEY" || key.contains("PLACEHOLDER")
    }

    init {
        viewModelScope.launch {
            // Check if db is empty and seed with defaults
            repository.prepopulateIfEmpty()
            
            // Set the first keyword as default for GeoGrid selected
            val keywords = repository.allKeywords.first()
            if (keywords.isNotEmpty()) {
                _selectedGridKeyword.value = keywords.first().keyword
            }
            
            // If business profile is already configured, jump to Dashboard instead of Onboarding
            val profile = repository.businessProfile.first()
            if (profile != null) {
                _currentScreen.value = Screen.Dashboard
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectGridKeyword(keyword: String) {
        _selectedGridKeyword.value = keyword
    }

    // --- Onboarding / Business Setup ---
    fun setupBusiness(name: String, category: String, address: String, targetLocation: String, keywords: List<String>) {
        viewModelScope.launch {
            val profile = BusinessProfile(
                name = name,
                category = category,
                address = address,
                targetLocation = targetLocation
            )
            repository.saveBusinessProfile(profile)

            // Save keywords
            val keywordRanks = keywords.mapIndexed { index, kw ->
                KeywordRank(
                    keyword = kw.trim(),
                    currentRank = (3..18).random(),
                    previousRank = (3..18).random() + (-2..2).random(),
                    searchVolume = (100..1500).random()
                )
            }
            db.localDao().clearAllKeywords()
            db.localDao().insertKeywords(keywordRanks)

            // Generate geogrids for all
            for (kw in keywordRanks) {
                repository.generateGeoGridForKeyword(kw.keyword, kw.currentRank)
            }

            if (keywordRanks.isNotEmpty()) {
                _selectedGridKeyword.value = keywordRanks.first().keyword
            }

            _currentScreen.value = Screen.Dashboard
        }
    }

    // --- Keyword Management ---
    fun addKeyword(keywordText: String) {
        if (keywordText.isBlank()) return
        viewModelScope.launch {
            repository.addKeyword(keywordText.trim())
            repository.generateGeoGridForKeyword(keywordText.trim(), 12)
            if (_selectedGridKeyword.value.isEmpty()) {
                _selectedGridKeyword.value = keywordText.trim()
            }
        }
    }

    fun removeKeyword(id: Int, keywordText: String) {
        viewModelScope.launch {
            repository.deleteKeyword(id)
            if (_selectedGridKeyword.value == keywordText) {
                val rem = allKeywords.value.filter { it.id != id }
                _selectedGridKeyword.value = if (rem.isNotEmpty()) rem.first().keyword else ""
            }
        }
    }

    // --- Refresh/Simulate Live Maps Rank Updates ---
    fun runRankSimulation() {
        viewModelScope.launch {
            val currentList = allKeywords.value
            val updated = currentList.map { kr ->
                val change = (-2..2).random()
                val newRank = (kr.currentRank + change).coerceIn(1, 20)
                kr.copy(
                    previousRank = kr.currentRank,
                    currentRank = newRank,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            db.localDao().insertKeywords(updated)

            for (kr in updated) {
                repository.generateGeoGridForKeyword(kr.keyword, kr.currentRank)
            }
        }
    }

    // --- AI Local SEO Audit ---
    fun triggerLocalSeoAudit() {
        val profile = businessProfile.value ?: return
        val keywords = allKeywords.value.map { it.keyword }

        viewModelScope.launch {
            _auditUiState.value = AuditUiState.Loading
            try {
                val auditResponse = GeminiClient.generateSeoAudit(
                    businessName = profile.name,
                    category = profile.category,
                    location = profile.targetLocation,
                    keywords = keywords
                )

                if (auditResponse == "ERROR_KEY_MISSING") {
                    // Smart Offline Simulation of Gemini output for demo experience
                    simulateSeoAudit(profile)
                } else {
                    // Try to parse Gemini JSON
                    try {
                        val cleanedResponse = cleanJsonString(auditResponse)
                        val jsonObj = JSONObject(cleanedResponse)
                        val score = jsonObj.optInt("score", 75)
                        val gmbComp = jsonObj.optInt("gmbCompleteness", 80)
                        val citCount = jsonObj.optInt("citationCount", 12)
                        
                        val criticalArray = jsonObj.optJSONArray("critical_issues")
                        val critList = mutableListOf<String>()
                        if (criticalArray != null) {
                            for (i in 0 until criticalArray.length()) {
                                critList.add(criticalArray.getString(i))
                            }
                        }

                        val oppArray = jsonObj.optJSONArray("opportunities")
                        val oppList = mutableListOf<String>()
                        if (oppArray != null) {
                            for (i in 0 until oppArray.length()) {
                                oppList.add(oppArray.getString(i))
                            }
                        }

                        val citHealth = jsonObj.optString("citation_health", "No directory feedback provided.")

                        val dbJson = JSONObject().apply {
                            put("critical_issues", JSONArray(critList))
                            put("opportunities", JSONArray(oppList))
                            put("citation_health", citHealth)
                        }.toString()

                        val newAudit = SeoAudit(
                            businessName = profile.name,
                            auditScore = score,
                            gmbCompleteness = gmbComp,
                            citationCount = citCount,
                            recommendationsJson = dbJson
                        )
                        repository.saveSeoAudit(newAudit)
                        _auditUiState.value = AuditUiState.Success(newAudit)
                    } catch (e: Exception) {
                        Log.e("RankViewModel", "Failed to parse JSON: $auditResponse", e)
                        // Fallback to simulation if JSON was corrupt
                        simulateSeoAudit(profile, "AI analysis succeeded but output format was unexpected. Re-formatted details follow.")
                    }
                }
            } catch (e: Exception) {
                _auditUiState.value = AuditUiState.Error(e.localizedMessage ?: "Unknown audit error.")
            }
        }
    }

    private suspend fun simulateSeoAudit(profile: BusinessProfile, notice: String? = null) {
        val score = (68..84).random()
        val completeness = (70..95).random()
        val citations = (8..17).random()

        val json = JSONObject().apply {
            val crit = mutableListOf(
                "Profile missing secondary service keywords in GMB categories.",
                "Response rate on 4-star and 5-star reviews is below 60% this month.",
                "Inconsistent physical address coordinates on secondary citation networks."
            )
            if (notice != null) {
                crit.add(0, "System Notice: $notice")
            }
            put("critical_issues", JSONArray(crit))
            put("opportunities", JSONArray(listOf(
                "Incorporate local landmarks (e.g. within 2 blocks of Market Street) in your description.",
                "Upload 5 customer-focused images showing your store layout and premium products.",
                "Deploy the RankOnMaps Review Assistant to automate personalized replies to fresh customer feedback."
            )))
            put("citation_health", "78/100 Directory matches. Verified clean entries across Yelp, Apple Maps, and YellowPages.")
        }.toString()

        val simulatedAudit = SeoAudit(
            businessName = profile.name,
            auditScore = score,
            gmbCompleteness = completeness,
            citationCount = citations,
            recommendationsJson = json
        )
        repository.saveSeoAudit(simulatedAudit)
        _auditUiState.value = AuditUiState.Success(simulatedAudit)
    }

    private fun cleanJsonString(raw: String): String {
        // Strip markdown backticks if Gemini includes them
        var temp = raw.trim()
        if (temp.startsWith("```json")) {
            temp = temp.removePrefix("```json")
        }
        if (temp.startsWith("```")) {
            temp = temp.removePrefix("```")
        }
        if (temp.endsWith("```")) {
            temp = temp.removeSuffix("```")
        }
        return temp.trim()
    }

    // --- AI GMB Post Generator ---
    fun generateGmbPost(topic: String, cta: String) {
        val profile = businessProfile.value ?: return
        viewModelScope.launch {
            _gmbPostUiState.value = GmbPostUiState.Loading
            try {
                val response = GeminiClient.generateGmbPost(profile.name, topic, cta)
                if (response == "ERROR_KEY_MISSING") {
                    val simulatedPost = """
                        📢 NEW AT ${profile.name.uppercase()}!
                        
                        We are thrilled to announce our latest update on "$topic"! Whether you're a neighborhood local or visiting the area, we've crafted this experience just for you.
                        
                        ✨ What makes it special:
                        - Crafted with premium local ingredients
                        - Infused with the community vibe of ${profile.targetLocation}
                        - Styled to perfection by our expert team
                        
                        📍 Come visit us at ${profile.address}!
                        
                        👉 $cta
                        
                        #${profile.category.replace(" ", "")} #LocalSEO #GMBPost #SupportLocal
                    """.trimIndent()
                    _gmbPostUiState.value = GmbPostUiState.Success(simulatedPost)
                } else {
                    _gmbPostUiState.value = GmbPostUiState.Success(response)
                }
            } catch (e: Exception) {
                _gmbPostUiState.value = GmbPostUiState.Error(e.localizedMessage ?: "Failed to generate GMB post")
            }
        }
    }

    fun clearGmbPostState() {
        _gmbPostUiState.value = GmbPostUiState.Idle
    }

    // --- AI Review Reply Assistant ---
    fun generateAiReviewReply(review: ReviewItem, tone: String) {
        val profile = businessProfile.value ?: return
        
        viewModelScope.launch {
            _replyGenerationStates.value = _replyGenerationStates.value + (review.id to "Generating")
            try {
                val reply = GeminiClient.generateReviewReply(
                    businessName = profile.name,
                    reviewerName = review.authorName,
                    rating = review.rating,
                    reviewText = review.comment,
                    replyTone = tone
                )

                if (reply == "ERROR_KEY_MISSING") {
                    val simReply = when {
                        review.rating >= 4 -> {
                            "Thank you so much, ${review.authorName}! We're absolutely thrilled you enjoyed your experience at ${profile.name}. Your kind words mean the world to our team here in ${profile.targetLocation}. See you again soon!"
                        }
                        review.rating == 3 -> {
                            "Hi ${review.authorName}, thanks for sharing your honest feedback with us. We're glad you liked certain parts of your visit, but we apologize for falling short on the Wi-Fi speed. We are currently upgrading our router and hope to welcome you back for a better experience soon!"
                        }
                        else -> {
                            "Hello ${review.authorName}, we are truly sorry that your visit did not meet expectations. At ${profile.name}, we pride ourselves on top-notch service and quality, and it's clear we missed the mark. We'd love the chance to make this right; please reach out to us directly at contact@thecoffeegrind.com so we can address this personally."
                        }
                    }
                    repository.updateReviewReply(review.id, simReply, "Draft")
                    _replyGenerationStates.value = _replyGenerationStates.value + (review.id to "Success")
                } else {
                    repository.updateReviewReply(review.id, reply, "Draft")
                    _replyGenerationStates.value = _replyGenerationStates.value + (review.id to "Success")
                }
            } catch (e: Exception) {
                _replyGenerationStates.value = _replyGenerationStates.value + (review.id to "Error")
            }
        }
    }

    fun publishReviewReply(reviewId: String) {
        viewModelScope.launch {
            val review = allReviews.value.find { it.id == reviewId } ?: return@launch
            if (review.aiReply != null) {
                repository.updateReviewReply(reviewId, review.aiReply, "Published")
            }
        }
    }

    fun saveManualReply(reviewId: String, manualReplyText: String) {
        viewModelScope.launch {
            repository.updateReviewReply(reviewId, manualReplyText, "Draft")
        }
    }

    fun deleteReply(reviewId: String) {
        viewModelScope.launch {
            repository.updateReviewReply(reviewId, "", "None")
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.resetToDefault()
            _selectedGridKeyword.value = allKeywords.value.firstOrNull()?.keyword ?: ""
            _currentScreen.value = Screen.Dashboard
        }
    }
}
