package com.dwyer.bandbuddy.data

import app.cash.sqldelight.db.SqlDriver
import com.dwyer.bandbuddy.Setlist
import com.dwyer.bandbuddy.SetlistItem
import com.dwyer.bandbuddy.database.BandBuddyDatabase
import kotlinx.datetime.LocalDate

class SqlDelightSetlistRepository(driver: SqlDriver) : SetlistRepository {
    private val database = BandBuddyDatabase(driver)
    private val queries = database.bandBuddyDatabaseQueries

    override suspend fun getAllSetlists(): List<Setlist> {
        return queries.getAllSetlists().executeAsList().map { dbSetlist ->
            Setlist(
                id = dbSetlist.id,
                name = dbSetlist.name,
                date = LocalDate.parse(dbSetlist.date),
                venue = dbSetlist.venue,
                items = emptyList() // Items loaded separately
            )
        }
    }

    override suspend fun getSetlistById(id: String): Setlist? {
        val dbSetlist = queries.getSetlistById(id).executeAsOneOrNull() ?: return null
        return Setlist(
            id = dbSetlist.id,
            name = dbSetlist.name,
            date = LocalDate.parse(dbSetlist.date),
            venue = dbSetlist.venue,
            items = emptyList() // Items loaded separately
        )
    }

    override suspend fun insertSetlist(setlist: Setlist) {
        queries.insertSetlist(
            id = setlist.id,
            name = setlist.name,
            date = setlist.date.toString(),
            venue = setlist.venue
        )
    }

    override suspend fun updateSetlist(setlist: Setlist) {
        queries.updateSetlist(
            name = setlist.name,
            date = setlist.date.toString(),
            venue = setlist.venue,
            id = setlist.id
        )
    }

    override suspend fun deleteSetlist(id: String) {
        queries.deleteSetlist(id)
    }
}

class SqlDelightSetlistItemRepository(driver: SqlDriver) : SetlistItemRepository {
    private val database = BandBuddyDatabase(driver)
    private val queries = database.bandBuddyDatabaseQueries

    override suspend fun getSetlistItems(setlistId: String): List<SetlistItem> {
        return queries.getSetlistItems(setlistId).executeAsList().map { dbItem ->
            SetlistItem(
                id = dbItem.id,
                setlistId = dbItem.setlistId,
                songId = dbItem.songId,
                order = dbItem.orderIndex.toInt(),
                notes = dbItem.notes,
                segue = dbItem.segue
            )
        }
    }

    override suspend fun getSetlistItemById(id: String): SetlistItem? {
        val dbItem = queries.getSetlistItemById(id).executeAsOneOrNull() ?: return null
        return SetlistItem(
            id = dbItem.id,
            setlistId = dbItem.setlistId,
            songId = dbItem.songId,
            order = dbItem.orderIndex.toInt(),
            notes = dbItem.notes,
            segue = dbItem.segue
        )
    }

    override suspend fun insertSetlistItem(item: SetlistItem) {
        queries.insertSetlistItem(
            id = item.id,
            setlistId = item.setlistId,
            songId = item.songId,
            orderIndex = item.order.toLong(),
            notes = item.notes,
            segue = item.segue
        )
    }

    override suspend fun updateSetlistItem(item: SetlistItem) {
        insertSetlistItem(item) // Using REPLACE semantics
    }

    override suspend fun updateSetlistItemOrder(id: String, newOrder: Int) {
        queries.updateSetlistItemOrder(newOrder.toLong(), id)
    }

    override suspend fun deleteSetlistItem(id: String) {
        queries.deleteSetlistItem(id)
    }

    override suspend fun deleteAllSetlistItems(setlistId: String) {
        queries.deleteAllSetlistItems(setlistId)
    }
}
