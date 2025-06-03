package com.dwyer.bandbuddy.data

import app.cash.sqldelight.db.SqlDriver
import com.dwyer.bandbuddy.domain.SongUseCase
import com.dwyer.bandbuddy.domain.SetlistUseCase

/**
 * Example of how to set up the SQLDelight repositories with proper drivers
 *
 * For Android:
 * val driver = AndroidSqliteDriver(BandBuddyDatabase.Schema, context, "bandbuddy.db")
 *
 * For iOS:
 * val driver = NativeSqliteDriver(BandBuddyDatabase.Schema, "bandbuddy.db")
 */
object RepositoryProvider {

    fun createUseCases(sqlDriver: SqlDriver): Pair<SongUseCase, SetlistUseCase> {
        val songRepository = DatabaseFactory.createSongRepository(sqlDriver)
        val setlistRepository = DatabaseFactory.createSetlistRepository(sqlDriver)
        val setlistItemRepository = DatabaseFactory.createSetlistItemRepository(sqlDriver)

        val songUseCase = SongUseCase(songRepository)
        val setlistUseCase = SetlistUseCase(setlistRepository, setlistItemRepository)

        return songUseCase to setlistUseCase
    }

    fun createInMemoryUseCases(): Pair<SongUseCase, SetlistUseCase> {
        val songRepository = InMemorySongRepository()
        val setlistRepository = InMemorySetlistRepository()
        val setlistItemRepository = InMemorySetlistItemRepository()

        val songUseCase = SongUseCase(songRepository)
        val setlistUseCase = SetlistUseCase(setlistRepository, setlistItemRepository)

        return songUseCase to setlistUseCase
    }
}