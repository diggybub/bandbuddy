package com.dwyer.bandbuddy.export

import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.Song

interface SetlistExporter {
    suspend fun exportToPdf(setlist: Setlist, songs: List<Song>, filePath: String): Boolean
    fun exportToText(setlist: Setlist, songs: List<Song>): String
    suspend fun shareViaEmail(setlist: Setlist, songs: List<Song>, recipientEmail: String): Boolean
}

data class ExportResult(
    val success: Boolean,
    val filePath: String? = null,
    val errorMessage: String? = null
)
