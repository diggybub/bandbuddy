package com.dwyer.bandbuddy.domain

import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.SetlistItem
import com.dwyer.bandbuddy.data.SetlistRepository
import com.dwyer.bandbuddy.data.SetlistItemRepository
import kotlinx.datetime.LocalDate
import kotlin.random.Random

class SetlistUseCase(
    private val setlistRepository: SetlistRepository,
    private val setlistItemRepository: SetlistItemRepository
) {
    
    suspend fun getAllSetlists(): List<Setlist> {
        return setlistRepository.getAllSetlists()
    }
    
    suspend fun getSetlistById(id: String): Setlist? {
        val setlist = setlistRepository.getSetlistById(id) ?: return null
        val items = setlistItemRepository.getSetlistItems(id)
        return setlist.copy(items = items)
    }
    
    suspend fun createSetlist(name: String, date: LocalDate, venue: String): Setlist {
        val setlist = Setlist(
            id = generateId("setlist"),
            name = name.trim(),
            date = date,
            venue = venue.trim(),
            items = emptyList()
        )
        setlistRepository.insertSetlist(setlist)
        return setlist
    }

    suspend fun updateSetlist(
        setlistId: String,
        name: String? = null,
        date: LocalDate? = null,
        venue: String? = null
    ): Setlist? {
        val existing = setlistRepository.getSetlistById(setlistId) ?: return null
        val updated = existing.copy(
            name = name?.trim() ?: existing.name,
            date = date ?: existing.date,
            venue = venue?.trim() ?: existing.venue
        )
        setlistRepository.updateSetlist(updated)
        return getSetlistById(setlistId)
    }

    suspend fun copySetlist(
        setlistId: String,
        newName: String,
        newDate: LocalDate,
        newVenue: String
    ): Setlist? {
        val originalSetlist = getSetlistById(setlistId) ?: return null

        // Create new setlist
        val newSetlist = createSetlist(newName, newDate, newVenue)

        // Copy all items
        originalSetlist.items.forEach { originalItem ->
            addSongToSetlist(
                setlistId = newSetlist.id,
                songId = originalItem.songId,
                notes = originalItem.notes,
                segue = originalItem.segue
            )
        }

        return getSetlistById(newSetlist.id)
    }

    suspend fun addSongToSetlist(setlistId: String, songId: String, notes: String = "", segue: String = ""): SetlistItem {
        val existingItems = setlistItemRepository.getSetlistItems(setlistId)
        val nextOrder = (existingItems.maxOfOrNull { it.order } ?: 0) + 1
        
        val item = SetlistItem(
            id = generateId("item"),
            setlistId = setlistId,
            songId = songId,
            order = nextOrder,
            notes = notes,
            segue = segue
        )
        setlistItemRepository.insertSetlistItem(item)
        return item
    }

    suspend fun updateSetlistItem(
        itemId: String,
        notes: String? = null,
        segue: String? = null
    ): SetlistItem? {
        val existing = setlistItemRepository.getSetlistItemById(itemId) ?: return null
        val updated = existing.copy(
            notes = notes ?: existing.notes,
            segue = segue ?: existing.segue
        )
        setlistItemRepository.updateSetlistItem(updated)
        return updated
    }

    suspend fun reorderSetlistItem(itemId: String, newOrder: Int) {
        setlistItemRepository.updateSetlistItemOrder(itemId, newOrder)
    }

    suspend fun removeSetlistItem(itemId: String) {
        setlistItemRepository.deleteSetlistItem(itemId)
    }

    suspend fun deleteSetlist(setlistId: String) {
        setlistItemRepository.deleteAllSetlistItems(setlistId)
        setlistRepository.deleteSetlist(setlistId)
    }

    suspend fun getSetlistsByDate(date: LocalDate): List<Setlist> {
        return getAllSetlists().filter { it.date == date }
    }

    suspend fun getUpcomingSetlists(): List<Setlist> {
        // For now, return all setlists - we'll implement proper date filtering later
        return getAllSetlists()
    }

    suspend fun getPastSetlists(): List<Setlist> {
        // For now, return empty list - we'll implement proper date filtering later
        return emptyList()
    }

    private fun generateId(prefix: String): String {
        return "${prefix}_${Random.nextLong()}"
    }
}
