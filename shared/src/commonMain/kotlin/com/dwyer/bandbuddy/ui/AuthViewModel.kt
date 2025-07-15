package com.dwyer.bandbuddy.ui

import com.dwyer.bandbuddy.Band
import com.dwyer.bandbuddy.User
import com.dwyer.bandbuddy.data.AuthRepository
import com.dwyer.bandbuddy.data.BandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val auth: AuthRepository,
    private val band: BandRepository
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        // Load current user when ViewModel is created
        _currentUser.value = auth.getCurrentUser()
    }

    suspend fun signIn(email: String, password: String) {
        _loading.value = true
        val result = auth.signIn(email, password)
        if (result.isSuccess) {
            _currentUser.value = result.getOrNull()
            _status.value = null
        } else {
            _status.value = result.exceptionOrNull()?.message
        }
        _loading.value = false
    }

    suspend fun signUp(
        email: String,
        password: String,
        displayName: String,
        bandName: String?
    ): Band? {
        _loading.value = true
        val result = auth.signUp(email, password, displayName)
        if (result.isFailure) {
            _status.value = result.exceptionOrNull()?.message
            _loading.value = false
            return null
        }
        val user = result.getOrNull()
        _currentUser.value = user
        if (user != null && !bandName.isNullOrBlank()) {
            val bandResult = band.createBand(bandName, user.id)
            if (bandResult.isSuccess) {
                _status.value = "Created account and band!"
                _loading.value = false
                return bandResult.getOrNull()
            } else {
                _status.value = bandResult.exceptionOrNull()?.message
            }
        } else {
            _status.value = "Signed up successfully."
        }
        _loading.value = false
        return null
    }

    fun clearStatus() {
        _status.value = null
    }

    suspend fun updateDisplayName(displayName: String) {
        val user = _currentUser.value
        if (user != null) {
            _loading.value = true
            val updatedUser = user.copy(displayName = displayName)
            val result = auth.updateUser(updatedUser)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _status.value = "Profile updated successfully"
            } else {
                _status.value = result.exceptionOrNull()?.message ?: "Update failed"
            }
            _loading.value = false
        }
    }

    suspend fun updateBandName(bandName: String) {
        val user = _currentUser.value
        val bandId = user?.bandId
        if (bandId != null) {
            _loading.value = true
            val currentBand = band.getBand(bandId)
            if (currentBand != null) {
                val updatedBand = currentBand.copy(name = bandName)
                val result = band.updateBand(updatedBand)
                if (result.isSuccess) {
                    _status.value = "Band name updated successfully"
                } else {
                    _status.value = result.exceptionOrNull()?.message ?: "Band update failed"
                }
            } else {
                _status.value = "Band not found"
            }
            _loading.value = false
        }
    }

    suspend fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }
}
