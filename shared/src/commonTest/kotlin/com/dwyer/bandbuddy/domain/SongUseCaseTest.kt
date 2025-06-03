package com.dwyer.bandbuddy.domain

import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.data.InMemorySongRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SongUseCaseTest {
    
    private lateinit var songUseCase: SongUseCase
    private lateinit var songRepository: InMemorySongRepository

    // @BeforeTest - JUnit 5 equivalent or manual call in each test
    private fun setup() { // Changed from @BeforeEach for clarity, ensure called in tests
        songRepository = InMemorySongRepository()
        songUseCase = SongUseCase(songRepository)
    }
    
    @Test
    fun `addSong should add a new song to the repository`() = runTest {
        setup() // Manually calling setup
        val title = "Test Song"
        val artist = "Test Artist"
        
        val addedSong = songUseCase.addSong(title, artist)
        
        assertEquals(title, addedSong.title)
        assertEquals(artist, addedSong.artist)
        assertEquals(SongStatus.TO_LEARN, addedSong.status)
        
        val songs = songUseCase.getAllSongs()
        assertEquals(1, songs.size)
        assertEquals(addedSong, songs.first())
    }
    
    @Test
    fun `updateSongStatus should update the status of an existing song`() = runTest {
        setup()
        val song = songUseCase.addSong("Song 1", "Artist 1")
        
        songUseCase.updateSongStatus(song.id, SongStatus.KNOWN)
        
        val updatedSong = songUseCase.getAllSongs().first()
        assertEquals(SongStatus.KNOWN, updatedSong.status)
    }
    
    @Test
    fun `deleteSong should remove a song from the repository`() = runTest {
        setup()
        val song1 = songUseCase.addSong("Song 1", "Artist 1")
        val song2 = songUseCase.addSong("Song 2", "Artist 2")
        
        songUseCase.deleteSong(song1.id)
        
        val songs = songUseCase.getAllSongs()
        assertEquals(1, songs.size)
        assertEquals(song2, songs.first())
    }
    
    @Test
    fun `getAllSongs should return songs sorted by artist then title`() = runTest {
        setup()
        songUseCase.addSong("Song C", "Artist B")
        songUseCase.addSong("Song A", "Artist A")
        songUseCase.addSong("Song B", "Artist A")
        
        val songs = songUseCase.getAllSongs()
        
        assertEquals("Song A", songs[0].title)
        assertEquals("Song B", songs[1].title)
        assertEquals("Song C", songs[2].title)
    }
    
    @Test
    fun `getGroupedByArtist should group songs correctly`() = runTest {
        setup()
        songUseCase.addSong("Song A", "Artist 1")
        songUseCase.addSong("Song B", "Artist 2")
        songUseCase.addSong("Song C", "Artist 1")
        
        val groupedSongs = songUseCase.getGroupedByArtist()
        
        assertEquals(2, groupedSongs.size)
        assertTrue(groupedSongs.containsKey("Artist 1"))
        assertTrue(groupedSongs.containsKey("Artist 2"))
        assertEquals(2, groupedSongs["Artist 1"]?.size)
        assertEquals(1, groupedSongs["Artist 2"]?.size)
    }
}
