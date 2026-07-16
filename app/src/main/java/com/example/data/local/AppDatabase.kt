package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        BusinessProfile::class,
        KeywordRank::class,
        GeoGridPoint::class,
        ReviewItem::class,
        SeoAudit::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localDao(): LocalDao
}
