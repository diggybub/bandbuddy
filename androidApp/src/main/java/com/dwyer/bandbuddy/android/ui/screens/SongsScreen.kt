package com.dwyer.bandbuddy.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.domain.SongUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen() {
    val songUseCase: SongUseCase = koinInject()
    val context = androidx.compose.ui.platform.LocalContext.current

    fun shareSongLibrary(songs: List<Song>) {
        val sb = StringBuilder()
        sb.appendLine("=".repeat(50))
        sb.appendLine("SONG LIBRARY")
        sb.appendLine("=".repeat(50))
        sb.appendLine()

        val groupedSongs = songs.sortedBy { it.artist }.groupBy { it.artist }

        groupedSongs.forEach { (artist, artistSongs) ->
            sb.appendLine("$artist:")
            artistSongs.sortedBy { it.title }.forEach { song ->
                val status = if (song.status == SongStatus.KNOWN) "[READY]" else "[LEARN]"
                sb.appendLine("  • ${song.title} $status")
            }
            sb.appendLine()
        }

        val knownCount = songs.count { it.status == SongStatus.KNOWN }
        val totalCount = songs.size
        val readyPercentage = if (totalCount > 0) (knownCount * 100) / totalCount else 0

        sb.appendLine("-".repeat(50))
        sb.appendLine("SUMMARY")
        sb.appendLine("-".repeat(50))
        sb.appendLine("Total Songs: $totalCount")
        sb.appendLine("Ready Songs: $knownCount")
        sb.appendLine("Readiness: $readyPercentage%")
        sb.appendLine("Artists: ${groupedSongs.size}")

        // Share the content
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Song Library")
            putExtra(android.content.Intent.EXTRA_TEXT, sb.toString())
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = android.content.Intent.createChooser(intent, "Share song library...")
            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooser)
    }

    var allSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var filteredSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var groupedSongs by remember { mutableStateOf<Map<String, List<Song>>>(emptyMap()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<SongStatus?>(null) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    val coroutineScope = rememberCoroutineScope()

    // Function to refresh songs
    suspend fun refreshSongs() {
        isLoading = true
        allSongs = songUseCase.getAllSongs()
        isLoading = false
    }

    // Function to filter songs based on search and status
    fun filterSongs() {
        filteredSongs = allSongs.filter { song ->
            val matchesSearch = searchQuery.isBlank() ||
                    song.title.contains(searchQuery, ignoreCase = true) ||
                    song.artist.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatus == null || song.status == selectedStatus
            matchesSearch && matchesStatus
        }

        // Update grouped songs
        groupedSongs = filteredSongs.groupBy { it.artist }.toSortedMap()
    }

    // Load songs when screen appears
    LaunchedEffect(Unit) {
        refreshSongs()
    }

    // Filter songs when search query or status changes
    LaunchedEffect(allSongs, searchQuery, selectedStatus) {
        filterSongs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Songs Library (${filteredSongs.size})",
                style = MaterialTheme.typography.headlineMedium
            )
            Row {
                // View mode toggle
                IconButton(
                    onClick = {
                        viewMode =
                            if (viewMode == ViewMode.LIST) ViewMode.GROUPED else ViewMode.LIST
                    }
                ) {
                    Text(if (viewMode == ViewMode.LIST) "📋" else "🎯")
                }
                // Share library button
                IconButton(
                    onClick = {
                        shareSongLibrary(allSongs)
                    },
                    enabled = allSongs.isNotEmpty()
                ) {
                    Text("📤")
                }
                Button(
                    onClick = { showAddDialog = true },
                    enabled = !isLoading
                ) {
                    Text("Add Song")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search and filter row
        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search songs...") },
                placeholder = { Text("Search by title or artist") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Text("🔍") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status filter chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { selectedStatus = if (selectedStatus == null) null else null },
                    label = { Text("All") },
                    selected = selectedStatus == null
                )
                FilterChip(
                    onClick = {
                        selectedStatus =
                            if (selectedStatus == SongStatus.KNOWN) null else SongStatus.KNOWN
                    },
                    label = { Text("Known (${allSongs.count { it.status == SongStatus.KNOWN }})") },
                    selected = selectedStatus == SongStatus.KNOWN
                )
                FilterChip(
                    onClick = {
                        selectedStatus =
                            if (selectedStatus == SongStatus.TO_LEARN) null else SongStatus.TO_LEARN
                    },
                    label = { Text("To Learn (${allSongs.count { it.status == SongStatus.TO_LEARN }})") },
                    selected = selectedStatus == SongStatus.TO_LEARN
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Mark all filtered songs as known
                        filteredSongs.forEach { song ->
                            if (song.status == SongStatus.TO_LEARN) {
                                songUseCase.updateSongStatus(song.id, SongStatus.KNOWN)
                            }
                        }
                        refreshSongs()
                    }
                },
                enabled = filteredSongs.any { it.status == SongStatus.TO_LEARN },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mark All Known")
            }

            OutlinedButton(
                onClick = {
                    // Generate random setlist from known songs
                    val knownSongs = filteredSongs.filter { it.status == SongStatus.KNOWN }
                    if (knownSongs.isNotEmpty()) {
                        val randomSetlist = knownSongs.shuffled().take(8)
                        // TODO: Create actual setlist
                    }
                },
                enabled = filteredSongs.count { it.status == SongStatus.KNOWN } >= 3,
                modifier = Modifier.weight(1f)
            ) {
                Text("Quick Setlist")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Debug: Add sample songs button (remove in production)
        if (allSongs.isEmpty() && !isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            songUseCase.addSong("Stairway to Heaven", "Led Zeppelin")
                            songUseCase.addSong("Black Dog", "Led Zeppelin")
                            songUseCase.addSong("Bohemian Rhapsody", "Queen")
                            songUseCase.addSong("We Will Rock You", "Queen")
                            songUseCase.addSong("Sweet Child O' Mine", "Guns N' Roses")
                            songUseCase.addSong("Paradise City", "Guns N' Roses")
                            songUseCase.addSong("Hotel California", "Eagles")
                            songUseCase.addSong("Take It Easy", "Eagles")
                            songUseCase.addSong("Smells Like Teen Spirit", "Nirvana")
                            songUseCase.addSong("Come As You Are", "Nirvana")
                            refreshSongs()
                        }
                    }
                ) {
                    Text("Add Sample Songs")
                }
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            filteredSongs.isEmpty() && searchQuery.isNotBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No songs found for \"$searchQuery\"")
                        TextButton(
                            onClick = { searchQuery = "" }
                        ) {
                            Text("Clear Search")
                        }
                    }
                }
            }

            allSongs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No songs yet. Add your first song!")
                }
            }
            else -> {
                if (viewMode == ViewMode.GROUPED) {
                    GroupedSongsList(
                        groupedSongs = groupedSongs,
                        onStatusChange = { song, newStatus ->
                            coroutineScope.launch {
                                songUseCase.updateSongStatus(song.id, newStatus)
                                refreshSongs()
                            }
                        }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSongs) { song ->
                            SongItem(
                                song = song,
                                onStatusChange = { newStatus ->
                                    coroutineScope.launch {
                                        songUseCase.updateSongStatus(song.id, newStatus)
                                        refreshSongs()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSongDialog(
            onDismiss = { showAddDialog = false },
            onAddSong = { title, artist ->
                coroutineScope.launch {
                    songUseCase.addSong(title, artist)
                    refreshSongs()
                }
                showAddDialog = false
            }
        )
    }
}

enum class ViewMode { LIST, GROUPED }

@Composable
fun GroupedSongsList(
    groupedSongs: Map<String, List<Song>>,
    onStatusChange: (Song, SongStatus) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedSongs.forEach { (artist, songs) ->
            // Artist header
            item(key = "header_$artist") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = artist,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${songs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Songs for this artist
            items(songs) { song ->
                SongItem(
                    song = song,
                    onStatusChange = { newStatus -> onStatusChange(song, newStatus) },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    onStatusChange: (SongStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilterChip(
                    onClick = {
                        val newStatus = if (song.status == SongStatus.KNOWN)
                            SongStatus.TO_LEARN else SongStatus.KNOWN
                        onStatusChange(newStatus)
                    },
                    label = {
                        Text(if (song.status == SongStatus.KNOWN) "Known" else "To Learn")
                    },
                    selected = song.status == SongStatus.KNOWN
                )
            }
        }
    }
}

@Composable
fun AddSongDialog(
    onDismiss: () -> Unit,
    onAddSong: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Song") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && artist.isNotBlank()) {
                        onAddSong(title, artist)
                    }
                },
                enabled = title.isNotBlank() && artist.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
