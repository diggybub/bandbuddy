package com.dwyer.bandbuddy

import kotlinx.datetime.Instant
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

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val bandId: String? = null,
    val role: UserRole = UserRole.MEMBER
)

enum class UserRole { ADMIN, MEMBER }

data class Band(
    val id: String,
    val name: String,
    val createdBy: String,
    val createdAt: Instant,
    val members: List<String> = emptyList()
)

data class BandInvite(
    val id: String,
    val bandId: String,
    val email: String,
    val invitedBy: String,
    val createdAt: Instant,
    val status: InviteStatus = InviteStatus.PENDING
)

enum class InviteStatus { PENDING, ACCEPTED, DECLINED }
