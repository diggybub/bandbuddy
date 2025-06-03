package com.dwyer.bandbuddy.data

import app.cash.sqldelight.db.SqlDriver
import com.dwyer.bandbuddy.database.BandBuddyDatabase

object DatabaseFactory {
    private var driver: SqlDriver? = null

    fun createDatabase(sqlDriver: SqlDriver): BandBuddyDatabase {
        driver = sqlDriver
        return BandBuddyDatabase(sqlDriver)
    }

    fun createSongRepository(sqlDriver: SqlDriver): SongRepository {
        return SqlDelightSongRepository(sqlDriver)
    }

    fun createSetlistRepository(sqlDriver: SqlDriver): SetlistRepository {
        return SqlDelightSetlistRepository(sqlDriver)
    }

    fun createSetlistItemRepository(sqlDriver: SqlDriver): SetlistItemRepository {
        return SqlDelightSetlistItemRepository(sqlDriver)
    }
}