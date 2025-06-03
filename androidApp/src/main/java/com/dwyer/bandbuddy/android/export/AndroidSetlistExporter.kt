package com.dwyer.bandbuddy.android.export

import android.content.Context
import android.content.Intent
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.Song
import com.dwyer.bandbuddy.SongStatus
import com.dwyer.bandbuddy.export.SetlistExporter
import java.io.File
import java.io.FileWriter

class AndroidSetlistExporter(private val context: Context) : SetlistExporter {

    override suspend fun exportToPdf(
        setlist: Setlist,
        songs: List<Song>,
        filePath: String
    ): Boolean {
        // For now, we'll export as text file with .pdf extension
        // In a real implementation, you'd use a proper PDF library
        return try {
            val textContent = exportToText(setlist, songs)
            val file = File(filePath.replace(".pdf", ".txt"))
            file.parentFile?.mkdirs()

            FileWriter(file).use { writer ->
                writer.write(textContent)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun exportToText(setlist: Setlist, songs: List<Song>): String {
        val sb = StringBuilder()

        sb.appendLine("=" * 50)
        sb.appendLine("SETLIST: ${setlist.name}")
        sb.appendLine("=" * 50)
        sb.appendLine()
        sb.appendLine("Venue: ${setlist.venue}")
        sb.appendLine("Date: ${setlist.date}")
        sb.appendLine()
        sb.appendLine("-" * 50)
        sb.appendLine()

        setlist.items.sortedBy { it.order }.forEach { item ->
            val song = songs.find { it.id == item.songId }
            if (song != null) {
                val status = if (song.status == SongStatus.KNOWN) "[READY]" else "[LEARN]"
                sb.appendLine("${item.order}. ${song.title} - ${song.artist} $status")

                if (item.notes.isNotBlank()) {
                    sb.appendLine("   Notes: ${item.notes}")
                }

                if (item.segue.isNotBlank()) {
                    sb.appendLine("   → ${item.segue}")
                }

                sb.appendLine()
            }
        }

        // Summary
        val knownSongs = setlist.items.count { item ->
            songs.find { it.id == item.songId }?.status == SongStatus.KNOWN
        }
        val totalSongs = setlist.items.size
        val readyPercentage = if (totalSongs > 0) (knownSongs * 100) / totalSongs else 0

        sb.appendLine("-" * 50)
        sb.appendLine("SUMMARY")
        sb.appendLine("-" * 50)
        sb.appendLine("Total Songs: $totalSongs")
        sb.appendLine("Ready Songs: $knownSongs")
        sb.appendLine("Readiness: $readyPercentage%")
        sb.appendLine("Estimated Duration: ${totalSongs * 3.5} minutes")

        return sb.toString()
    }

    override suspend fun shareViaEmail(
        setlist: Setlist,
        songs: List<Song>,
        recipientEmail: String
    ): Boolean {
        return try {
            val textContent = exportToText(setlist, songs)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                putExtra(Intent.EXTRA_SUBJECT, "Setlist: ${setlist.name}")
                putExtra(Intent.EXTRA_TEXT, textContent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "Share setlist via...")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun shareAsText(setlist: Setlist, songs: List<Song>): Boolean {
        return try {
            val textContent = exportToText(setlist, songs)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Setlist: ${setlist.name}")
                putExtra(Intent.EXTRA_TEXT, textContent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "Share setlist...")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exportToFile(setlist: Setlist, songs: List<Song>, filePath: String): Boolean {
        return try {
            val textContent = exportToText(setlist, songs)
            val file = File(filePath)
            file.parentFile?.mkdirs()

            FileWriter(file).use { writer ->
                writer.write(textContent)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

private operator fun String.times(count: Int): String = repeat(count)
