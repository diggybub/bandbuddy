package com.dwyer.bandbuddy.data

import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.SetlistItem

class InMemorySongRepository : SongRepository {
    private val songs = mutableListOf<Song>()

    override suspend fun getAllSongs(): List<Song> {
        return songs.sortedWith(compareBy({ it.artist }, { it.title }))
    }

    override suspend fun getSongsByArtist(artist: String): List<Song> {
        return songs.filter { it.artist == artist }.sortedBy { it.title }
    }

    override suspend fun insertSong(song: Song) {
        songs.removeAll { it.id == song.id }
        songs.add(song)
    }

    override suspend fun updateSong(song: Song) {
        val index = songs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            songs[index] = song
        }
    }

    override suspend fun deleteSong(songId: String) {
        songs.removeAll { it.id == songId }
    }
}

class InMemorySetlistRepository : SetlistRepository {
    private val setlists = mutableListOf<Setlist>()

    override suspend fun getAllSetlists(): List<Setlist> {
        return setlists.sortedByDescending { it.date }
    }

    override suspend fun getSetlistById(id: String): Setlist? {
        return setlists.find { it.id == id }
    }

    override suspend fun insertSetlist(setlist: Setlist) {
        setlists.removeAll { it.id == setlist.id }
        setlists.add(setlist)
    }

    override suspend fun updateSetlist(setlist: Setlist) {
        val index = setlists.indexOfFirst { it.id == setlist.id }
        if (index != -1) {
            setlists[index] = setlist
        }
    }

    override suspend fun deleteSetlist(id: String) {
        setlists.removeAll { it.id == id }
    }
}

class InMemorySetlistItemRepository : SetlistItemRepository {
    private val items = mutableListOf<SetlistItem>()

    override suspend fun getSetlistItems(setlistId: String): List<SetlistItem> {
        return items.filter { it.setlistId == setlistId }.sortedBy { it.order }
    }

    override suspend fun getSetlistItemById(id: String): SetlistItem? {
        return items.find { it.id == id }
    }

    override suspend fun insertSetlistItem(item: SetlistItem) {
        items.removeAll { it.id == item.id }
        items.add(item)
    }

    override suspend fun updateSetlistItem(item: SetlistItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
        }
    }

    override suspend fun updateSetlistItemOrder(id: String, newOrder: Int) {
        val index = items.indexOfFirst { it.id == id }
        if (index != -1) {
            items[index] = items[index].copy(order = newOrder)
        }
    }

    override suspend fun deleteSetlistItem(id: String) {
        items.removeAll { it.id == id }
    }

    override suspend fun deleteAllSetlistItems(setlistId: String) {
        items.removeAll { it.setlistId == setlistId }
    }
}
