package com.dwyer.bandbuddy.data

import com.dwyer.bandbuddy.Band
import com.dwyer.bandbuddy.BandInvite
import com.dwyer.bandbuddy.User

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, displayName: String): Result<User>
    fun getCurrentUser(): User?
    suspend fun updateUser(user: User): Result<User>
    suspend fun signOut()
}

interface BandRepository {
    suspend fun createBand(name: String, createdBy: String): Result<Band>
    suspend fun getBand(id: String): Band?
    suspend fun updateBand(band: Band): Result<Band>
    suspend fun getBandMembers(bandId: String): List<User>
    suspend fun inviteToBand(bandId: String, email: String, invitedBy: String): Result<BandInvite>
    suspend fun respondToInvite(inviteId: String, accept: Boolean): Result<Unit>
    suspend fun getUserInvites(userEmail: String): List<BandInvite>
}
