package com.dwyer.bandbuddy

import com.dwyer.bandbuddy.di.initKoin
import com.dwyer.bandbuddy.domain.SongUseCase
import com.dwyer.bandbuddy.domain.SetlistUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Example class showing how to use Koin dependency injection
 * This can be used as a reference for UI components
 */
class BandBuddyApp : KoinComponent {

    // Inject dependencies using Koin
    private val songUseCase: SongUseCase by inject()
    private val setlistUseCase: SetlistUseCase by inject()

    suspend fun initializeApp() {
        // Example usage of injected dependencies
        val songs = songUseCase.getAllSongs()
        val setlists = setlistUseCase.getAllSetlists()

        println("App initialized with ${songs.size} songs and ${setlists.size} setlists")
    }

    companion object {
        /**
         * Initialize the app with in-memory repositories
         */
        fun create(): BandBuddyApp {
            initKoin()
            return BandBuddyApp()
        }

        /**
         * Initialize the app with SQLDelight database
         * Usage: BandBuddyApp.createWithDatabase(sqlDriver)
         */
        /*
        fun createWithDatabase(sqlDriver: SqlDriver): BandBuddyApp {
            startKoin {
                modules(createSqlDelightModules(sqlDriver))
            }
            return BandBuddyApp()
        }
        */
    }
}