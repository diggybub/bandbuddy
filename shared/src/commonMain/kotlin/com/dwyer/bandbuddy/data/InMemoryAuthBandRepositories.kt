package com.dwyer.bandbuddy.data

import com.dwyer.bandbuddy.Band
import com.dwyer.bandbuddy.BandInvite
import com.dwyer.bandbuddy.InviteStatus
import com.dwyer.bandbuddy.User
import com.dwyer.bandbuddy.UserRole
import kotlinx.datetime.Clock

class InMemoryAuthRepository : AuthRepository {
    private val users = mutableMapOf<String, User>()
    private var signedInUserId: String? = null

    override suspend fun signIn(email: String, password: String): Result<User> {
        val user = users.values.find { it.email == email }
        return if (user != null) {
            signedInUserId = user.id
            Result.success(user)
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        if (users.values.any { it.email == email }) {
            return Result.failure(Exception("Email already used"))
        }
        val user = User(
            id = randomId(),
            email = email,
            displayName = displayName,
        )
        users[user.id] = user
        signedInUserId = user.id
        return Result.success(user)
    }

    override fun getCurrentUser(): User? {
        return signedInUserId?.let { users[it] }
    }

    override suspend fun updateUser(user: User): Result<User> {
        users[user.id] = user
        // Update current user if this is the signed-in user
        if (signedInUserId == user.id) {
            signedInUserId = user.id
        }
        return Result.success(user)
    }

    override suspend fun signOut() {
        signedInUserId = null
    }

    fun allUsers(): Collection<User> = users.values
}

class InMemoryBandRepository(
    private val authRepository: InMemoryAuthRepository
) : BandRepository {
    private val bands = mutableMapOf<String, Band>()
    private val invites = mutableMapOf<String, BandInvite>()

    override suspend fun createBand(name: String, createdBy: String): Result<Band> {
        val band = Band(
            id = randomId(),
            name = name,
            createdBy = createdBy,
            createdAt = Clock.System.now(),
            members = listOf(createdBy)
        )
        bands[band.id] = band
        val user = authRepository.allUsers().find { it.id == createdBy }
        if (user != null) {
            authRepository.updateUser(user.copy(bandId = band.id, role = UserRole.ADMIN))
        }
        return Result.success(band)
    }

    override suspend fun getBand(id: String): Band? = bands[id]

    override suspend fun updateBand(band: Band): Result<Band> {
        bands[band.id] = band
        return Result.success(band)
    }

    override suspend fun getBandMembers(bandId: String): List<User> {
        val band = bands[bandId]
        return if (band == null) emptyList()
        else authRepository.allUsers().filter { it.bandId == bandId }
    }

    override suspend fun inviteToBand(
        bandId: String,
        email: String,
        invitedBy: String
    ): Result<BandInvite> {
        val invite = BandInvite(
            id = randomId(),
            bandId = bandId,
            email = email,
            invitedBy = invitedBy,
            createdAt = Clock.System.now(),
            status = InviteStatus.PENDING
        )
        invites[invite.id] = invite
        return Result.success(invite)
    }

    override suspend fun respondToInvite(inviteId: String, accept: Boolean): Result<Unit> {
        val invite = invites[inviteId] ?: return Result.failure(Exception("Invite not found"))
        val user =
            authRepository.allUsers().find { it.email == invite.email } ?: return Result.failure(
                Exception("No user for invited email")
            )
        if (accept) {
            val band = bands[invite.bandId]
            if (band != null) {
                bands[invite.bandId] = band.copy(members = band.members + user.id)
                authRepository.updateUser(user.copy(bandId = band.id, role = UserRole.MEMBER))
                invites[inviteId] = invite.copy(status = InviteStatus.ACCEPTED)
            }
        } else {
            invites[inviteId] = invite.copy(status = InviteStatus.DECLINED)
        }
        return Result.success(Unit)
    }

    override suspend fun getUserInvites(userEmail: String): List<BandInvite> {
        return invites.values.filter { it.email == userEmail && it.status == InviteStatus.PENDING }
    }
}

private fun randomId(): String =
    (1..16).map { ('a'..'z') + ('0'..'9') }.flatten().shuffled().take(16).joinToString("")
