package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationWrapper(viewModel: RankViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val businessProfile by viewModel.businessProfile.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (currentScreen != Screen.Onboarding) {
                NavigationBar(
                    containerColor = SurfaceSlateVariant,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Dashboard,
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryEmerald,
                            selectedTextColor = PrimaryEmerald,
                            indicatorColor = SurfaceSlate
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.GeoGrid,
                        onClick = { viewModel.navigateTo(Screen.GeoGrid) },
                        icon = { Icon(Icons.Default.Map, contentDescription = "GeoGrid") },
                        label = { Text("GeoGrid") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryEmerald,
                            selectedTextColor = PrimaryEmerald,
                            indicatorColor = SurfaceSlate
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.AIReviews,
                        onClick = { viewModel.navigateTo(Screen.AIReviews) },
                        icon = { Icon(Icons.Default.RateReview, contentDescription = "Reviews") },
                        label = { Text("Reviews") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryEmerald,
                            selectedTextColor = PrimaryEmerald,
                            indicatorColor = SurfaceSlate
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.AIAudit,
                        onClick = { viewModel.navigateTo(Screen.AIAudit) },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = "AI Audit") },
                        label = { Text("AI Audit") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryEmerald,
                            selectedTextColor = PrimaryEmerald,
                            indicatorColor = SurfaceSlate
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Settings,
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryEmerald,
                            selectedTextColor = PrimaryEmerald,
                            indicatorColor = SurfaceSlate
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundObsidian)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    Screen.Onboarding -> OnboardingScreen(viewModel)
                    Screen.Dashboard -> DashboardScreen(viewModel)
                    Screen.GeoGrid -> GeoGridScreen(viewModel)
                    Screen.AIReviews -> AIReviewsScreen(viewModel)
                    Screen.AIAudit -> AIAuditScreen(viewModel)
                    Screen.Settings -> SettingsScreen(viewModel)
                }
            }
        }
    }
}

// ==========================================
// 1. ONBOARDING SCREEN
// ==========================================
@Composable
fun OnboardingScreen(viewModel: RankViewModel) {
    var bizName by remember { mutableStateOf("") }
    var bizCategory by remember { mutableStateOf("") }
    var bizAddress by remember { mutableStateOf("") }
    var bizLocation by remember { mutableStateOf("") }
    var keywordsInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // App Logo Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    Brush.radialGradient(listOf(PrimaryEmerald.copy(alpha = 0.3f), Color.Transparent)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = "App Icon",
                tint = PrimaryEmerald,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "RankOnMaps AI",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Track search visibility grids, analyze competitors, and optimize local search ranking using Google Gemini.",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Onboarding Form Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Configure Business Profile",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = bizName,
                    onValueChange = { bizName = it },
                    label = { Text("Business Name (e.g., The Coffee Grind)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("biz_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    )
                )

                OutlinedTextField(
                    value = bizCategory,
                    onValueChange = { bizCategory = it },
                    label = { Text("Primary Category (e.g., Coffee Shop)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("biz_category_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    )
                )

                OutlinedTextField(
                    value = bizAddress,
                    onValueChange = { bizAddress = it },
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    )
                )

                OutlinedTextField(
                    value = bizLocation,
                    onValueChange = { bizLocation = it },
                    label = { Text("Target Location (e.g., Financial District, SF)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    )
                )

                OutlinedTextField(
                    value = keywordsInput,
                    onValueChange = { keywordsInput = it },
                    label = { Text("Target Keywords (comma separated)") },
                    placeholder = { Text("specialty coffee, best espresso, lattea") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    )
                )

                Button(
                    onClick = {
                        val kws = if (keywordsInput.isBlank()) {
                            listOf("specialty coffee", "best cafe", "espresso")
                        } else {
                            keywordsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        }
                        viewModel.setupBusiness(
                            name = bizName.ifBlank { "The Coffee Grind" },
                            category = bizCategory.ifBlank { "Specialty Coffee Shop" },
                            address = bizAddress.ifBlank { "525 Market St, San Francisco, CA 94105" },
                            targetLocation = bizLocation.ifBlank { "Financial District, SF" },
                            keywords = kws
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("setup_business_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Launch Optimization Dashboard",
                        color = BackgroundObsidian,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BackgroundObsidian)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// 2. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(viewModel: RankViewModel) {
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val keywords by viewModel.allKeywords.collectAsStateWithLifecycle()
    var showAddKeywordDialog by remember { mutableStateOf(false) }
    var newKeywordText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Upper Header Section
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = profile?.name ?: "Local Business",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = profile?.targetLocation ?: "Configure location",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }

            IconButton(
                onClick = { viewModel.runRankSimulation() },
                modifier = Modifier
                    .background(SurfaceSlate, CircleShape)
                    .testTag("refresh_rankings_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Simulate rank changes", tint = PrimaryEmerald)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Core Dashboard Summary Metrics Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val avgRank = if (keywords.isNotEmpty()) String.format("%.1f", keywords.map { it.currentRank }.average()) else "--"
            val improvedCount = keywords.count { it.currentRank < krPrev(it) }
            
            DashboardMetricCard(
                title = "Average Rank",
                value = avgRank,
                subtitle = "Google Maps Top 20",
                icon = Icons.Default.TrendingUp,
                color = AccentCyan,
                modifier = Modifier.weight(1f)
            )

            DashboardMetricCard(
                title = "Rank Improvements",
                value = "$improvedCount keywords",
                subtitle = "Since last crawl",
                icon = Icons.Default.Bolt,
                color = PrimaryEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom Trendline Chart (Drawn inside Compose Canvas)
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Maps Visibility Trend",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Averaged search ranking positions over time",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(PrimaryEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Live tracking",
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Canvas Drawing for Ranking Graph
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path()
                        // Ranks are inverted in charts (lower position number is better!)
                        val dataPoints = listOf(14f, 11f, 9f, 10f, 7f, 6f, 4.5f)
                        val maxVal = 20f
                        val minVal = 1f
                        
                        val width = size.width
                        val height = size.height
                        
                        val stepX = width / (dataPoints.size - 1)
                        val scaleY = height / (maxVal - minVal)

                        // Draw background horizontal gridlines
                        for (i in 0..4) {
                            val yVal = i * (height / 4)
                            drawLine(
                                color = Color.DarkGray.copy(alpha = 0.3f),
                                start = Offset(0f, yVal),
                                end = Offset(width, yVal),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Generate path points
                        dataPoints.forEachIndexed { index, value ->
                            val x = index * stepX
                            // Invert: higher rank value (1) should be drawn at the top (0y), lower rank (20) at the bottom (height y)
                            val y = (value - minVal) * scaleY
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                            
                            // Draw point circles
                            drawCircle(
                                color = PrimaryEmerald,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        // Draw main trend path
                        drawPath(
                            path = path,
                            color = PrimaryEmerald,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // X-Axis labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
                    days.forEach {
                        Text(it, style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Keywords Rankings Table Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tracked Google Maps Keywords",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )

            Button(
                onClick = { showAddKeywordDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceSlate),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Keyword", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Keywords List Card
        if (keywords.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Keywords Tracked", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Add your search keywords above to track Maps rankings.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    keywords.forEach { kw ->
                        KeywordRankRow(kw, onDelete = { viewModel.removeKeyword(kw.id, kw.keyword) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add Keyword Dialog
    if (showAddKeywordDialog) {
        AlertDialog(
            onDismissRequest = { showAddKeywordDialog = false },
            containerColor = SurfaceSlate,
            title = { Text("Add Maps Keyword", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newKeywordText,
                    onValueChange = { newKeywordText = it },
                    label = { Text("Keyword (e.g., craft cold brew)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    ),
                    modifier = Modifier.testTag("add_keyword_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKeywordText.isNotBlank()) {
                            viewModel.addKeyword(newKeywordText)
                            newKeywordText = ""
                            showAddKeywordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.testTag("add_keyword_button")
                ) {
                    Text("Add", color = BackgroundObsidian)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKeywordDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

private fun krPrev(kr: KeywordRank): Int = if (kr.previousRank <= 0) 15 else kr.previousRank

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
        }
    }
}

@Composable
fun KeywordRankRow(keywordRank: KeywordRank, onDelete: () -> Unit) {
    val improvement = krPrev(keywordRank) - keywordRank.currentRank

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                keywordRank.keyword,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
            Text(
                "Volume: ${keywordRank.searchVolume}/mo",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .background(
                        when {
                            keywordRank.currentRank <= 3 -> PrimaryEmerald.copy(alpha = 0.15f)
                            keywordRank.currentRank <= 10 -> AccentCyan.copy(alpha = 0.15f)
                            else -> GlowOrange.copy(alpha = 0.15f)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "#${keywordRank.currentRank}",
                    color = when {
                        keywordRank.currentRank <= 3 -> GlowGreen
                        keywordRank.currentRank <= 10 -> AccentCyan
                        else -> GlowOrange
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Rank Shift Arrow
            if (improvement > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = "Improved", tint = GlowGreen, modifier = Modifier.size(16.dp))
                    Text("+$improvement", color = GlowGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else if (improvement < 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingDown, contentDescription = "Dropped", tint = GlowRed, modifier = Modifier.size(16.dp))
                    Text("$improvement", color = GlowRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Text("—", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Keyword", tint = Color.DarkGray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ==========================================
// 3. GEOGRID MAP TRACKER SCREEN
// ==========================================
@Composable
fun GeoGridScreen(viewModel: RankViewModel) {
    val keywords by viewModel.allKeywords.collectAsStateWithLifecycle()
    val selectedKeyword by viewModel.selectedGridKeyword.collectAsStateWithLifecycle()
    val gridPoints by viewModel.geoGridPointsForSelectedKeyword.collectAsStateWithLifecycle()

    var activeGridPoint by remember { mutableStateOf<GeoGridPoint?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "GeoGrid Rank Tracker",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
        Text(
            "Visualize your Google Maps placement across precise localized coordinates.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Keyword Selector Scrollable Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            keywords.forEach { kw ->
                val isSelected = kw.keyword == selectedKeyword
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) PrimaryEmerald else SurfaceSlate,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            viewModel.selectGridKeyword(kw.keyword)
                            activeGridPoint = null
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        kw.keyword,
                        color = if (isSelected) BackgroundObsidian else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Virtual Map Representation + GeoGrid
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlateVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // drawBehind adds a cool virtual blueprint map style grid
                    .drawBehind {
                        val gridGap = 40.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(Color.DarkGray.copy(alpha = 0.15f), Offset(x, 0f), Offset(x, size.height), 1f)
                            x += gridGap
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(Color.DarkGray.copy(alpha = 0.15f), Offset(0f, y), Offset(size.width, y), 1f)
                            y += gridGap
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "3x3 Visibility Grid",
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // GeoGrid points container
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (y in 0 until 3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                for (x in 0 until 3) {
                                    val point = gridPoints.find { it.gridX == x && it.gridY == y }
                                    GeoGridDot(point) {
                                        activeGridPoint = point
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Detail Panel for selected GeoGrid point
        AnimatedVisibility(
            visible = activeGridPoint != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            activeGridPoint?.let { point ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(getRankColor(point.rank), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Coordinate Grid Point (X: ${point.gridX}, Y: ${point.gridY})",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            IconButton(onClick = { activeGridPoint = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Maps Rank Position: #${point.rank}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = getRankColor(point.rank),
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (point.rank > 3) {
                            Text(
                                text = "Competitor in Local Pack: ${point.competitorName ?: "Unknown Competitor"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = GlowOrange),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "💡 Optimization Tip: Competitor is outperforming you at this local coordinate. Build more local NAP citations to bridge the authority gap.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = "🎉 You rank in the local pack! Keep driving fresh high-rating reviews here to solidify dominance.",
                                style = MaterialTheme.typography.bodySmall.copy(color = GlowGreen),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeoGridDot(point: GeoGridPoint?, onClick: () -> Unit) {
    val rank = point?.rank ?: -1
    val dotColor = getRankColor(rank)

    Box(
        modifier = Modifier
            .size(44.dp)
            .background(dotColor.copy(alpha = 0.15f), CircleShape)
            .border(2.dp, dotColor, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (rank == -1) "U" else "$rank",
            color = dotColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

fun getRankColor(rank: Int): Color {
    return when {
        rank <= 0 -> Color.Gray
        rank <= 3 -> GlowGreen
        rank <= 10 -> AccentCyan
        else -> GlowOrange
    }
}

// ==========================================
// 4. AI REVIEW REPLIES ASSISTANT
// ==========================================
@Composable
fun AIReviewsScreen(viewModel: RankViewModel) {
    val reviews by viewModel.allReviews.collectAsStateWithLifecycle()
    val replyStates by viewModel.replyGenerationStates.collectAsStateWithLifecycle()

    var activeReviewForReply by remember { mutableStateOf<ReviewItem?>(null) }
    var selectedTone by remember { mutableStateOf("Professional") }
    var draftReplyText by remember { mutableStateOf("") }

    val tones = listOf("Professional", "Empathetic", "Appreciative")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "AI Review Assistant",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
        Text(
            "Draft personalized, SEO-optimized replies to Google Maps customer reviews instantly using Gemini.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No customer reviews found.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reviews) { review ->
                    ReviewCard(
                        review = review,
                        status = replyStates[review.id] ?: "Idle",
                        onReplyAction = {
                            activeReviewForReply = review
                            draftReplyText = review.aiReply ?: ""
                        },
                        onPublish = { viewModel.publishReviewReply(review.id) },
                        onDeleteReply = { viewModel.deleteReply(review.id) }
                    )
                }
            }
        }
    }

    // AI Review Reply Drafting Bottom-Sheet/Dialog
    if (activeReviewForReply != null) {
        val review = activeReviewForReply!!
        val generationStatus = replyStates[review.id] ?: "Idle"

        AlertDialog(
            onDismissRequest = { activeReviewForReply = null },
            containerColor = SurfaceSlate,
            title = {
                Text(
                    "AI Review Reply Draft",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Review by ${review.authorName}",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(review.rating) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GlowOrange, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        "\"${review.comment}\"",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                        modifier = Modifier.padding(vertical = 8.dp).verticalScroll(rememberScrollState())
                    )

                    HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

                    Text("Pick AI Tone:", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tones.forEach { tone ->
                            val isToneSelected = tone == selectedTone
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isToneSelected) PrimaryEmerald else SurfaceSlateVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedTone = tone }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    tone,
                                    fontSize = 11.sp,
                                    color = if (isToneSelected) BackgroundObsidian else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (generationStatus == "Generating") {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryEmerald)
                        }
                    } else {
                        OutlinedTextField(
                            value = draftReplyText,
                            onValueChange = { draftReplyText = it },
                            label = { Text("Draft AI Response") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.generateAiReviewReply(review, selectedTone)
                            // Wait for flow update
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        modifier = Modifier.testTag("generate_reply_button")
                    ) {
                        Text("Ask Gemini", color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.saveManualReply(review.id, draftReplyText)
                            activeReviewForReply = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                    ) {
                        Text("Save Draft", color = BackgroundObsidian)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { activeReviewForReply = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )

        // Observe the generated reply as it arrives in state
        LaunchedEffect(reviews) {
            val updatedReview = reviews.find { it.id == review.id }
            if (updatedReview != null && updatedReview.aiReply != null && updatedReview.aiReply != draftReplyText) {
                draftReplyText = updatedReview.aiReply
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: ReviewItem,
    status: String,
    onReplyAction: () -> Unit,
    onPublish: () -> Unit,
    onDeleteReply: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Review Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(review.authorName, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(review.rating) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GlowOrange, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(review.reviewDate, style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
                    }
                }

                // Rating status tag
                Box(
                    modifier = Modifier
                        .background(
                            if (review.rating >= 4) GlowGreen.copy(alpha = 0.15f) else GlowRed.copy(alpha = 0.15f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (review.rating >= 4) "Positive" else "Negative",
                        color = if (review.rating >= 4) GlowGreen else GlowRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "\"${review.comment}\"",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
            )

            // Reply section
            if (review.aiReply.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onReplyAction,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceSlateVariant),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Draft AI Reply", color = Color.White, fontSize = 11.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                // Beautiful inner card containing the AI reply
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlateVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (review.replyStatus == "Published") "Published Response" else "AI Reply Draft",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PrimaryEmerald
                                )
                            }

                            Row {
                                IconButton(onClick = onReplyAction, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Reply", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                }
                                IconButton(onClick = onDeleteReply, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Reply", tint = GlowRed, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            review.aiReply,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                        )

                        if (review.replyStatus != "Published") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onPublish,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Publish Reply", color = BackgroundObsidian, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. AI SEO AUDIT & POST WRITER SCREEN
// ==========================================
@Composable
fun AIAuditScreen(viewModel: RankViewModel) {
    val auditState by viewModel.auditUiState.collectAsStateWithLifecycle()
    val gmbPostUiState by viewModel.gmbPostUiState.collectAsStateWithLifecycle()
    val isDemoMode = viewModel.isApiKeyMissing

    var postTopic by remember { mutableStateOf("") }
    var postCta by remember { mutableStateOf("Learn More") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            "AI Local SEO Audit",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
        Text(
            "Deploy AI audits to check Google Business Profile compliance and optimize GMB copywriting.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )

        if (isDemoMode) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = GlowOrange.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = GlowOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Demo Mode: API Key missing. Simulation activated.",
                        color = GlowOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Trigger SEO Audit Row
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Google Maps SEO Compliance",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Text(
                    "Our AI inspects category relevance, holiday setups, business descriptions, and local NAP compliance.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.triggerLocalSeoAudit() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_audit_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BackgroundObsidian, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Complete Audit", color = BackgroundObsidian, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Audit state processing
        when (val state = auditState) {
            AuditUiState.Idle -> {
                // Show standard placeholders or previous audit from DB if exists
                val dbAudit by viewModel.latestAudit.collectAsStateWithLifecycle()
                if (dbAudit != null) {
                    AuditResultsView(dbAudit!!)
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Click 'Trigger Complete Audit' to generate recommendations.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            AuditUiState.Loading -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryEmerald)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Gemini is auditing local parameters...", color = Color.White)
                    }
                }
            }
            is AuditUiState.Success -> {
                AuditResultsView(state.audit)
            }
            is AuditUiState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GlowRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Audit Generation Failed", color = GlowRed, fontWeight = FontWeight.Bold)
                        Text(state.message, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // AI GMB POST WRITER MODULE
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "AI GMB Post Writer",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Text(
                    "Generate fresh, keyword-rich Google Business Profile post updates to drive local search engagement.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = postTopic,
                    onValueChange = { postTopic = it },
                    label = { Text("What is this post about? (e.g. Free pastry with latte)") },
                    modifier = Modifier.fillMaxWidth().testTag("gmb_post_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = PrimaryEmerald
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Call to Action Button:", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Learn More", "Order Online", "Book Appointment", "Call Now").forEach { cta ->
                        val isSelected = cta == postCta
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) PrimaryEmerald else SurfaceSlateVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { postCta = cta }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                cta,
                                fontSize = 10.sp,
                                color = if (isSelected) BackgroundObsidian else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (postTopic.isNotBlank()) {
                            viewModel.generateGmbPost(postTopic, postCta)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_gmb_post_button")
                ) {
                    Text("Generate Post Copy", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Post output
                when (val pState = gmbPostUiState) {
                    GmbPostUiState.Idle -> {}
                    GmbPostUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryEmerald)
                        }
                    }
                    is GmbPostUiState.Success -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceSlateVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("AI Post Result", fontWeight = FontWeight.Bold, color = PrimaryEmerald, fontSize = 12.sp)
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("GMB Post", pState.postText)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied GMB post copy!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.CopyAll, contentDescription = "Copy", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    pState.postText,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                                )
                            }
                        }
                    }
                    is GmbPostUiState.Error -> {
                        Text("Failed to generate post: ${pState.message}", color = GlowRed, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AuditResultsView(audit: SeoAudit) {
    var criticalList = remember { mutableStateListOf<String>() }
    var opportunitiesList = remember { mutableStateListOf<String>() }
    var citationHealthStr by remember { mutableStateOf("") }

    // Parse recommendations JSON
    LaunchedEffect(audit) {
        criticalList.clear()
        opportunitiesList.clear()
        try {
            val jsonObj = JSONObject(audit.recommendationsJson)
            val crit = jsonObj.optJSONArray("critical_issues")
            if (crit != null) {
                for (i in 0 until crit.length()) {
                    criticalList.add(crit.getString(i))
                }
            }
            val opp = jsonObj.optJSONArray("opportunities")
            if (opp != null) {
                for (i in 0 until opp.length()) {
                    opportunitiesList.add(opp.getString(i))
                }
            }
            citationHealthStr = jsonObj.optString("citation_health", "")
        } catch (e: Exception) {
            criticalList.add("Profile parameters require review.")
            opportunitiesList.add("No opportunities loaded.")
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Circular Audit Score Gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidthPx = 8.dp.toPx()
                            // Background circle
                            drawCircle(
                                color = Color.DarkGray.copy(alpha = 0.3f),
                                radius = size.minDimension / 2 - strokeWidthPx / 2,
                                style = Stroke(width = strokeWidthPx)
                            )
                            // Filled arc representing score percentage
                            val sweepAngle = (audit.auditScore.toFloat() / 100f) * 360f
                            drawArc(
                                color = PrimaryEmerald,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidthPx)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${audit.auditScore}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text("Score", color = Color.Gray, fontSize = 10.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("SEO Audit Performance: ${audit.businessName}", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Citation Coverage: ${audit.citationCount} Directories", fontSize = 12.sp, color = AccentCyan)
                        Text("GBP Completeness Score: ${audit.gmbCompleteness}%", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (citationHealthStr.isNotEmpty()) {
                    Text(
                        "NAP Citation Consistency Details:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        citationHealthStr,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Warnings block
                if (criticalList.isNotEmpty()) {
                    Text(
                        "⚠️ CRITICAL ISSUES:",
                        style = MaterialTheme.typography.bodySmall.copy(color = GlowRed, fontWeight = FontWeight.Bold)
                    )
                    criticalList.forEach { issue ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ", color = GlowRed, fontWeight = FontWeight.Bold)
                            Text(issue, color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Opportunities block
                if (opportunitiesList.isNotEmpty()) {
                    Text(
                        "💡 OPTIMIZATION OPPORTUNITIES:",
                        style = MaterialTheme.typography.bodySmall.copy(color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                    )
                    opportunitiesList.forEach { opp ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ", color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                            Text(opp, color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(viewModel: RankViewModel) {
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Account Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
        Text(
            "Manage business profile targets, active parameters, or clear developer databases.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Profile Information",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text("BUSINESS NAME", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(profile?.name ?: "--", color = Color.White, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(12.dp))

                Text("PRIMARY CATEGORY", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(profile?.category ?: "--", color = Color.White)

                Spacer(modifier = Modifier.height(12.dp))

                Text("ADDRESS", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(profile?.address ?: "--", color = Color.LightGray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Text("TARGET AREA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(profile?.targetLocation ?: "--", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // System configuration reset
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "System Operations",
                    fontWeight = FontWeight.Bold,
                    color = GlowRed,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    "This clears the database, seeds default coffee shop rankings, and resets the onboarding setup.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = {
                        viewModel.resetDatabase()
                        Toast.makeText(context, "Database successfully restored to default!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlowRed.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = GlowRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restore Default Seed Data", color = GlowRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Details
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlateVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("RankOnMaps AI Optimizer", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text("Version 1.0.0 • Google AI Studio Core App", color = Color.Gray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
