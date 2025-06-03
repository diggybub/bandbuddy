package com.dwyer.bandbuddy.data

import app.cash.sqldelight.db.SqlDriver
import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.database.BandBuddyDatabase

class SqlDelightSongRepository(driver: SqlDriver) : SongRepository {
    private val database = BandBuddyDatabase(driver)
    private val queries = database.bandBuddyDatabaseQueries

    override suspend fun getAllSongs(): List<Song> {
        return queries.getAllSongs().executeAsList().map { dbSong ->
            Song(
                id = dbSong.id,
                title = dbSong.title,
                artist = dbSong.artist,
                status = SongStatus.valueOf(dbSong.status)
            )
        }
    }

    override suspend fun getSongsByArtist(artist: String): List<Song> {
        return queries.getSongsByArtist(artist).executeAsList().map { dbSong ->
            Song(
                id = dbSong.id,
                title = dbSong.title,
                artist = dbSong.artist,
                status = SongStatus.valueOf(dbSong.status)
            )
        }
    }

    override suspend fun insertSong(song: Song) {
        queries.insertSong(
            id = song.id,
            title = song.title,
            artist = song.artist,
            status = song.status.name
        )
    }

    override suspend fun updateSong(song: Song) {
        // For updates, we can use the same insert since we have REPLACE semantics
        insertSong(song)
    }

    override suspend fun deleteSong(songId: String) {
        queries.deleteSong(songId)
    }
}