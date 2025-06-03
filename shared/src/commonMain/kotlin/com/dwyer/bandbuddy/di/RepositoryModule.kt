package com.dwyer.bandbuddy.di

import com.dwyer.bandbuddy.data.InMemorySongRepository
import com.dwyer.bandbuddy.data.InMemorySetlistRepository
import com.dwyer.bandbuddy.data.InMemorySetlistItemRepository
import com.dwyer.bandbuddy.data.SongRepository
import com.dwyer.bandbuddy.data.SetlistRepository
import com.dwyer.bandbuddy.data.SetlistItemRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<SongRepository> { InMemorySongRepository() }
    single<SetlistRepository> { InMemorySetlistRepository() }
    single<SetlistItemRepository> { InMemorySetlistItemRepository() }
}

// Enhanced repository module with persistence
val persistentRepositoryModule = module {
    // TODO: Add SQLDelight-based repositories when LocalDate issues are resolved
    // For now, use enhanced in-memory repositories that persist data
    single<SongRepository> { EnhancedInMemorySongRepository() }
    single<SetlistRepository> { InMemorySetlistRepository() }
    single<SetlistItemRepository> { InMemorySetlistItemRepository() }
}

// Enhanced in-memory repository that simulates persistence
class EnhancedInMemorySongRepository : SongRepository {
    companion object {
        private val songs = mutableListOf<com.dwyer.bandbuddy.Song>()
    }

    override suspend fun getAllSongs(): List<com.dwyer.bandbuddy.Song> {
        return songs.sortedWith(compareBy({ it.artist }, { it.title }))
    }

    override suspend fun getSongsByArtist(artist: String): List<com.dwyer.bandbuddy.Song> {
        return songs.filter { it.artist == artist }.sortedBy { it.title }
    }

    override suspend fun insertSong(song: com.dwyer.bandbuddy.Song) {
        songs.removeAll { it.id == song.id }
        songs.add(song)
    }

    override suspend fun updateSong(song: com.dwyer.bandbuddy.Song) {
        val index = songs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            songs[index] = song
        }
    }

    override suspend fun deleteSong(songId: String) {
        songs.removeAll { it.id == songId }
    }
}
