package com.dwyer.bandbuddy.android.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.domain.SongUseCase
import com.dwyer.bandbuddy.domain.SetlistUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class SetlistSong(
    val song: Song,
    val order: Int,
    val notes: String = "",
    val segue: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistBuilderScreen(
    setlistId: String? = null,
    setlistName: String = "New Setlist",
    onBack: () -> Unit = {}
) {
    val songUseCase: SongUseCase = koinInject()
    val setlistUseCase: SetlistUseCase = koinInject()
    var availableSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var setlistSongs by remember { mutableStateOf<List<SetlistSong>>(emptyList()) }
    var currentSetlist by remember { mutableStateOf<Setlist?>(null) }
    var showSongPicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var draggedItem by remember { mutableStateOf<SetlistSong?>(null) }
    var draggedOverIndex by remember { mutableStateOf(-1) }
    val coroutineScope = rememberCoroutineScope()

    // Load data
    LaunchedEffect(setlistId) {
        isLoading = true
        availableSongs = songUseCase.getAllSongs()

        if (setlistId != null) {
            currentSetlist = setlistUseCase.getSetlistById(setlistId)
            currentSetlist?.let { setlist ->
                setlistSongs = setlist.items.map { item ->
                    val song = availableSongs.find { it.id == item.songId }
                    if (song != null) {
                        SetlistSong(song, item.order, item.notes, item.segue)
                    } else {
                        null
                    }
                }.filterNotNull()
            }
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentSetlist?.name ?: setlistName,
                style = MaterialTheme.typography.headlineMedium
            )
            Row {
                TextButton(onClick = onBack) {
                    Text("Back")
                }
                Button(
                    onClick = { showSongPicker = true },
                    enabled = !isLoading
                ) {
                    Text("Add Song")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Setlist info card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Setlist Overview",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Songs: ${setlistSongs.size}")
                    Text("Duration: ~${setlistSongs.size * 3.5}min")
                }
                if (setlistSongs.isNotEmpty()) {
                    val knownSongs = setlistSongs.count { it.song.status == SongStatus.KNOWN }
                    val percentage = (knownSongs * 100) / setlistSongs.size
                    Text("Ready: $knownSongs/${setlistSongs.size} ($percentage%)")
                }

                currentSetlist?.let { setlist ->
                    Text("Venue: ${setlist.venue}")
                    Text("Date: ${setlist.date}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        if (setlistSongs.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "💡 Long press and drag to reorder songs",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Setlist content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            setlistSongs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your setlist is empty",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add songs to build your setlist",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showSongPicker = true }
                        ) {
                            Text("Add First Song")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = setlistSongs.sortedBy { it.order },
                        key = { _, item -> item.song.id }
                    ) { index, setlistSong ->
                        val isDragging = draggedItem == setlistSong
                        val elevation by animateDpAsState(
                            targetValue = if (isDragging) 8.dp else 0.dp,
                            label = "elevation"
                        )

                        DraggableSetlistSongItem(
                            setlistSong = setlistSong,
                            isDragging = isDragging,
                            elevation = elevation,
                            onDragStart = { draggedItem = it },
                            onDragEnd = { fromIndex, toIndex ->
                                if (fromIndex != toIndex) {
                                    // Reorder the list
                                    val mutableList = setlistSongs.toMutableList()
                                    val item = mutableList.removeAt(fromIndex)
                                    mutableList.add(toIndex, item)

                                    // Update orders
                                    setlistSongs = mutableList.mapIndexed { idx, song ->
                                        song.copy(order = idx + 1)
                                    }

                                    // TODO: Persist to database
                                }
                                draggedItem = null
                                draggedOverIndex = -1
                            },
                            onRemove = {
                                coroutineScope.launch {
                                    if (setlistId != null) {
                                        currentSetlist?.items?.find {
                                            it.songId == setlistSong.song.id && it.order == setlistSong.order
                                        }?.let { item ->
                                            setlistUseCase.removeSetlistItem(item.id)
                                        }
                                    }

                                    setlistSongs = setlistSongs.filter { it != setlistSong }
                                        .mapIndexed { idx, song ->
                                            song.copy(order = idx + 1)
                                        }
                                }
                            },
                            onNotesChange = { notes ->
                                coroutineScope.launch {
                                    if (setlistId != null) {
                                        currentSetlist?.items?.find {
                                            it.songId == setlistSong.song.id && it.order == setlistSong.order
                                        }?.let { item ->
                                            setlistUseCase.updateSetlistItem(item.id, notes = notes)
                                        }
                                    }

                                    setlistSongs = setlistSongs.map {
                                        if (it == setlistSong) it.copy(notes = notes) else it
                                    }
                                }
                            },
                            onSegueChange = { segue ->
                                coroutineScope.launch {
                                    if (setlistId != null) {
                                        currentSetlist?.items?.find {
                                            it.songId == setlistSong.song.id && it.order == setlistSong.order
                                        }?.let { item ->
                                            setlistUseCase.updateSetlistItem(item.id, segue = segue)
                                        }
                                    }

                                    setlistSongs = setlistSongs.map {
                                        if (it == setlistSong) it.copy(segue = segue) else it
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Song picker dialog
    if (showSongPicker) {
        SongPickerDialog(
            songs = availableSongs.filter { available ->
                searchQuery.isBlank() ||
                        available.title.contains(searchQuery, ignoreCase = true) ||
                        available.artist.contains(searchQuery, ignoreCase = true)
            },
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onSongSelected = { song ->
                coroutineScope.launch {
                    val nextOrder = (setlistSongs.maxOfOrNull { it.order } ?: 0) + 1

                    if (setlistId != null) {
                        setlistUseCase.addSongToSetlist(setlistId, song.id)
                    }

                    setlistSongs = setlistSongs + SetlistSong(song, nextOrder)
                    showSongPicker = false
                    searchQuery = ""
                }
            },
            onDismiss = {
                showSongPicker = false
                searchQuery = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraggableSetlistSongItem(
    setlistSong: SetlistSong,
    isDragging: Boolean,
    elevation: Dp,
    onDragStart: (SetlistSong) -> Unit,
    onDragEnd: (Int, Int) -> Unit,
    onRemove: () -> Unit,
    onNotesChange: (String) -> Unit,
    onSegueChange: (String) -> Unit
) {
    var showNotesDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(8.dp))
            .graphicsLayer {
                scaleX = if (isDragging) 1.05f else 1f
                scaleY = if (isDragging) 1.05f else 1f
            }
            .zIndex(if (isDragging) 1f else 0f)
            .pointerInput(setlistSong) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart(setlistSong) },
                    onDragEnd = { /* handled in parent */ }
                ) { _, _ -> }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drag handle
                Text(
                    text = "⋮⋮",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${setlistSong.order}.",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.width(32.dp)
                    )
                    Column {
                        Text(
                            text = setlistSong.song.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = setlistSong.song.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status indicator
                FilterChip(
                    onClick = { },
                    label = {
                        Text(if (setlistSong.song.status == SongStatus.KNOWN) "✓" else "?")
                    },
                    selected = setlistSong.song.status == SongStatus.KNOWN,
                    enabled = false
                )
            }

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { showNotesDialog = true }) {
                    Text("Notes")
                }
                TextButton(onClick = onRemove) {
                    Text("Remove")
                }
            }

            // Show notes/segue if present
            if (setlistSong.notes.isNotBlank()) {
                Text(
                    text = "📝 ${setlistSong.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (setlistSong.segue.isNotBlank()) {
                Text(
                    text = "→ ${setlistSong.segue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Notes dialog
    if (showNotesDialog) {
        NotesDialog(
            notes = setlistSong.notes,
            segue = setlistSong.segue,
            onNotesChange = onNotesChange,
            onSegueChange = onSegueChange,
            onDismiss = { showNotesDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongPickerDialog(
    songs: List<Song>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSongSelected: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Song to Setlist") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    label = { Text("Search songs...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    itemsIndexed(songs) { index, song ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            onClick = { onSongSelected(song) }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesDialog(
    notes: String,
    segue: String,
    onNotesChange: (String) -> Unit,
    onSegueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempNotes by remember { mutableStateOf(notes) }
    var tempSegue by remember { mutableStateOf(segue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Song Notes & Segue") },
        text = {
            Column {
                OutlinedTextField(
                    value = tempNotes,
                    onValueChange = { tempNotes = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Performance notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tempSegue,
                    onValueChange = { tempSegue = it },
                    label = { Text("Segue to next song") },
                    placeholder = { Text("Transition notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onNotesChange(tempNotes)
                    onSegueChange(tempSegue)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
