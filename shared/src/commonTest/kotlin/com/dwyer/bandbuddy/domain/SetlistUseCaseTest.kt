package com.dwyer.bandbuddy.domain

import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.data.InMemorySetlistRepository
import com.dwyer.bandbuddy.data.InMemorySetlistItemRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SetlistUseCaseTest {

    private lateinit var setlistUseCase: SetlistUseCase
    private lateinit var setlistRepository: InMemorySetlistRepository
    private lateinit var setlistItemRepository: InMemorySetlistItemRepository

    private fun setup() {
        setlistRepository = InMemorySetlistRepository()
        setlistItemRepository = InMemorySetlistItemRepository()
        setlistUseCase = SetlistUseCase(setlistRepository, setlistItemRepository)
    }

    @Test
    fun `createSetlist should create a new setlist`() = runTest {
        setup()
        val name = "Test Setlist"
        val date = LocalDate(2024, 1, 15)
        val venue = "Test Venue"

        val createdSetlist = setlistUseCase.createSetlist(name, date, venue)

        assertEquals(name, createdSetlist.name)
        assertEquals(date, createdSetlist.date)
        assertEquals(venue, createdSetlist.venue)
        assertTrue(createdSetlist.items.isEmpty())

        val allSetlists = setlistUseCase.getAllSetlists()
        assertEquals(1, allSetlists.size)
        assertEquals(createdSetlist.id, allSetlists.first().id)
    }

    @Test
    fun `addSongToSetlist should add song to setlist`() = runTest {
        setup()
        val setlist = setlistUseCase.createSetlist("Test", LocalDate(2024, 1, 15), "Venue")
        val songId = "song_123"

        val addedItem =
            setlistUseCase.addSongToSetlist(setlist.id, songId, "test notes", "test segue")

        assertEquals(setlist.id, addedItem.setlistId)
        assertEquals(songId, addedItem.songId)
        assertEquals(1, addedItem.order)
        assertEquals("test notes", addedItem.notes)
        assertEquals("test segue", addedItem.segue)
    }

    @Test
    fun `copySetlist should create a copy with new details`() = runTest {
        setup()
        val original =
            setlistUseCase.createSetlist("Original", LocalDate(2024, 1, 15), "Original Venue")
        setlistUseCase.addSongToSetlist(original.id, "song1", "notes1", "segue1")
        setlistUseCase.addSongToSetlist(original.id, "song2", "notes2", "segue2")

        val copy = setlistUseCase.copySetlist(
            original.id,
            "Copy Setlist",
            LocalDate(2024, 2, 15),
            "Copy Venue"
        )

        assertNotNull(copy)
        assertEquals("Copy Setlist", copy.name)
        assertEquals(LocalDate(2024, 2, 15), copy.date)
        assertEquals("Copy Venue", copy.venue)
        assertEquals(2, copy.items.size)

        // Verify all setlists exist
        val allSetlists = setlistUseCase.getAllSetlists()
        assertEquals(2, allSetlists.size)
    }

    @Test
    fun `deleteSetlist should remove setlist and all items`() = runTest {
        setup()
        val setlist = setlistUseCase.createSetlist("Test", LocalDate(2024, 1, 15), "Venue")
        setlistUseCase.addSongToSetlist(setlist.id, "song1")
        setlistUseCase.addSongToSetlist(setlist.id, "song2")

        setlistUseCase.deleteSetlist(setlist.id)

        val allSetlists = setlistUseCase.getAllSetlists()
        assertTrue(allSetlists.isEmpty())

        val items = setlistItemRepository.getSetlistItems(setlist.id)
        assertTrue(items.isEmpty())
    }
}