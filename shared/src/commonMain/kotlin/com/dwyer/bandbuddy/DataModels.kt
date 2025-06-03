package com.dwyer.bandbuddy

import kotlinx.datetime.LocalDate

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val status: SongStatus = SongStatus.TO_LEARN
)

enum class SongStatus { KNOWN, TO_LEARN }

data class Setlist(
    val id: String,
    val name: String,
    val date: LocalDate,
    val venue: String,
    val items: List<SetlistItem>
)

data class SetlistItem(
    val id: String,
    val setlistId: String,
    val songId: String,
    val order: Int,
    val notes: String = "",
    val segue: String = ""
)
