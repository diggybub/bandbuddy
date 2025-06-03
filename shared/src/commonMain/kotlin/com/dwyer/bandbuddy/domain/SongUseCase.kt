package com.dwyer.bandbuddy.domain

import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.data.SongRepository
import kotlin.random.Random

class SongUseCase(private val songRepository: SongRepository) {
    
    suspend fun getAllSongs(): List<Song> {
        return songRepository.getAllSongs()
    }
    
    suspend fun getSongsByArtist(artist: String): List<Song> {
        return songRepository.getSongsByArtist(artist)
    }
    
    suspend fun addSong(title: String, artist: String, status: SongStatus = SongStatus.TO_LEARN): Song {
        val song = Song(
            id = generateId(),
            title = title.trim(),
            artist = artist.trim(),
            status = status
        )
        songRepository.insertSong(song)
        return song
    }
    
    suspend fun updateSongStatus(songId: String, status: SongStatus) {
        val song = songRepository.getAllSongs().find { it.id == songId }
        if (song != null) {
            val updatedSong = song.copy(status = status)
            songRepository.updateSong(updatedSong)
        }
    }
    
    suspend fun deleteSong(songId: String) {
        songRepository.deleteSong(songId)
    }
    
    suspend fun getGroupedByArtist(): Map<String, List<Song>> {
        return getAllSongs().groupBy { it.artist }
    }
    
    private fun generateId(): String {
        return "song_${Random.nextLong()}"
    }
}
