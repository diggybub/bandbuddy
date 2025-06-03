package com.dwyer.bandbuddy.data

import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.SetlistItem

interface SongRepository {
    suspend fun getAllSongs(): List<Song>
    suspend fun getSongsByArtist(artist: String): List<Song>
    suspend fun insertSong(song: Song)
    suspend fun updateSong(song: Song)
    suspend fun deleteSong(songId: String)
}

interface SetlistRepository {
    suspend fun getAllSetlists(): List<Setlist>
    suspend fun getSetlistById(id: String): Setlist?
    suspend fun insertSetlist(setlist: Setlist)
    suspend fun updateSetlist(setlist: Setlist)
    suspend fun deleteSetlist(id: String)
}

interface SetlistItemRepository {
    suspend fun getSetlistItems(setlistId: String): List<SetlistItem>
    suspend fun getSetlistItemById(id: String): SetlistItem?
    suspend fun insertSetlistItem(item: SetlistItem)
    suspend fun updateSetlistItem(item: SetlistItem)
    suspend fun updateSetlistItemOrder(id: String, newOrder: Int)
    suspend fun deleteSetlistItem(id: String)
    suspend fun deleteAllSetlistItems(setlistId: String)
}
