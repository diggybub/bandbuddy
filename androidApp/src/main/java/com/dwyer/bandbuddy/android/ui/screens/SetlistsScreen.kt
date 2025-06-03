package com.dwyer.bandbuddy.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.domain.SetlistUseCase
import com.dwyer.bandbuddy.domain.SongUseCase
import com.dwyer.bandbuddy.export.SetlistExporter
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistsScreen() {
    val setlistUseCase: SetlistUseCase = koinInject()
    var setlists by remember { mutableStateOf<List<Setlist>>(emptyList()) }
    var showSetlistBuilder by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedSetlist by remember { mutableStateOf<Setlist?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Function to refresh setlists
    suspend fun refreshSetlists() {
        isLoading = true
        setlists = setlistUseCase.getAllSetlists()
        isLoading = false
    }

    // Load setlists when screen appears
    LaunchedEffect(Unit) {
        refreshSetlists()
    }

    // Show setlist builder if a setlist is selected
    if (showSetlistBuilder && selectedSetlist != null) {
        SetlistBuilderScreen(
            setlistId = selectedSetlist!!.id,
            setlistName = selectedSetlist!!.name,
            onBack = {
                showSetlistBuilder = false
                selectedSetlist = null
                coroutineScope.launch { refreshSetlists() }
            }
        )
    } else {
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
                    text = "Setlists (${setlists.size})",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(
                    onClick = { showCreateDialog = true },
                    enabled = !isLoading
                ) {
                    Text("New Setlist")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                setlists.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No setlists yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Create your first setlist to get started")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showCreateDialog = true }
                            ) {
                                Text("Create First Setlist")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(setlists) { setlist ->
                            SetlistCard(
                                setlist = setlist,
                                onEdit = {
                                    selectedSetlist = setlist
                                    showSetlistBuilder = true
                                },
                                onCopy = { setlistToCopy ->
                                    coroutineScope.launch {
                                        val today = LocalDate(2024, 1, 1) // Simplified for now
                                        setlistUseCase.copySetlist(
                                            setlistId = setlistToCopy.id,
                                            newName = "${setlistToCopy.name} (Copy)",
                                            newDate = today,
                                            newVenue = setlistToCopy.venue
                                        )
                                        refreshSetlists()
                                    }
                                },
                                onDelete = { setlistToDelete ->
                                    coroutineScope.launch {
                                        setlistUseCase.deleteSetlist(setlistToDelete.id)
                                        refreshSetlists()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSetlistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, date, venue ->
                coroutineScope.launch {
                    val newSetlist = setlistUseCase.createSetlist(name, date, venue)
                    selectedSetlist = newSetlist
                    showSetlistBuilder = true
                    showCreateDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistCard(
    setlist: Setlist,
    onEdit: () -> Unit,
    onCopy: (Setlist) -> Unit,
    onDelete: (Setlist) -> Unit
) {
    val setlistExporter: SetlistExporter = koinInject()
    val setlistUseCase: SetlistUseCase = koinInject()
    val songUseCase: SongUseCase = koinInject()
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = setlist.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = setlist.venue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = setlist.date.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    TextButton(
                        onClick = { showOptionsMenu = true }
                    ) {
                        Text("⋮")
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showOptionsMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = {
                                showOptionsMenu = false
                                showShareDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy") },
                            onClick = {
                                showOptionsMenu = false
                                onCopy(setlist)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showOptionsMenu = false
                                onDelete(setlist)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${setlist.items.size} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (setlist.items.isNotEmpty()) {
                    Text(
                        text = "~${setlist.items.size * 3.5}min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Share dialog
    if (showShareDialog) {
        ShareSetlistDialog(
            setlist = setlist,
            onDismiss = { showShareDialog = false },
            onShareText = {
                coroutineScope.launch {
                    val fullSetlist = setlistUseCase.getSetlistById(setlist.id)
                    if (fullSetlist != null) {
                        val songs = songUseCase.getAllSongs()
                        (setlistExporter as? com.dwyer.bandbuddy.android.export.AndroidSetlistExporter)
                            ?.shareAsText(fullSetlist, songs)
                    }
                }
            },
            onShareEmail = { email ->
                coroutineScope.launch {
                    val fullSetlist = setlistUseCase.getSetlistById(setlist.id)
                    if (fullSetlist != null) {
                        val songs = songUseCase.getAllSongs()
                        setlistExporter.shareViaEmail(fullSetlist, songs, email)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSetlistDialog(
    setlist: Setlist,
    onDismiss: () -> Unit,
    onShareText: () -> Unit,
    onShareEmail: (String) -> Unit
) {
    var emailAddress by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf(ShareOption.TEXT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Setlist") },
        text = {
            Column {
                // Share options
                Text(
                    text = "Choose sharing method:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { selectedOption = ShareOption.TEXT },
                        label = { Text("Text") },
                        selected = selectedOption == ShareOption.TEXT,
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        onClick = { selectedOption = ShareOption.EMAIL },
                        label = { Text("Email") },
                        selected = selectedOption == ShareOption.EMAIL,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedOption) {
                    ShareOption.TEXT -> {
                        Text(
                            text = "Share as formatted text via any app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    ShareOption.EMAIL -> {
                        OutlinedTextField(
                            value = emailAddress,
                            onValueChange = { emailAddress = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("bandmate@example.com") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when (selectedOption) {
                        ShareOption.TEXT -> onShareText()
                        ShareOption.EMAIL -> {
                            if (emailAddress.isNotBlank()) {
                                onShareEmail(emailAddress)
                            }
                        }
                    }
                    onDismiss()
                },
                enabled = when (selectedOption) {
                    ShareOption.TEXT -> true
                    ShareOption.EMAIL -> emailAddress.isNotBlank()
                }
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSetlistDialog(
    onDismiss: () -> Unit,
    onCreate: (String, LocalDate, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Use current date as default
    val today = LocalDate(2024, 1, 1) // Simplified for now
    var selectedDate by remember { mutableStateOf(today) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDays() * 24 * 60 * 60 * 1000L
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Setlist") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Setlist Name") },
                    placeholder = { Text("e.g., Blues Night at Joe's") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    placeholder = { Text("e.g., Joe's Bar") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Date picker button
                OutlinedTextField(
                    value = selectedDate.toString(),
                    onValueChange = { },
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Text("📅")
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && venue.isNotBlank()) {
                        onCreate(name, selectedDate, venue)
                    }
                },
                enabled = name.isNotBlank() && venue.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { dateMillis ->
                dateMillis?.let {
                    selectedDate = LocalDate.fromEpochDays((it / (24 * 60 * 60 * 1000L)).toInt())
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

enum class ShareOption { TEXT, EMAIL }
