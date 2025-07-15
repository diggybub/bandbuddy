package com.dwyer.bandbuddy.ui

import com.dwyer.bandbuddy.Band
import com.dwyer.bandbuddy.User
import com.dwyer.bandbuddy.data.AuthRepository
import com.dwyer.bandbuddy.data.BandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val auth: AuthRepository,
    private val band: BandRepository
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _currentBand = MutableStateFlow<Band?>(null)
    val currentBand: StateFlow<Band?> = _currentBand

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _currentUser.value = auth.getCurrentUser()
        // Don't load band data here since it's suspend
    }

    private suspend fun loadBandData() {
        val bandId = _currentUser.value?.bandId
        if (bandId != null) {
            _currentBand.value = band.getBand(bandId)
        } else {
            _currentBand.value = null
        }
    }

    suspend fun refreshBandData() {
        val bandId = _currentUser.value?.bandId
        if (bandId != null) {
            _currentBand.value = band.getBand(bandId)
        } else {
            _currentBand.value = null
        }
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
                    _currentBand.value = result.getOrNull()
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
        _currentBand.value = null
    }

    fun clearStatus() {
        _status.value = null
    }
}