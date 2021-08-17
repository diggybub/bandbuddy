package com.dwyer.setlist.home

import com.dwyer.setlist.database.DataSource
import com.dwyer.setlist.database.local.LocalDatabaseManager

class HomePresenter {
    val dataSource: DataSource = LocalDatabaseManager()
    // If no songs in the bank, show button to add songs to bank
    // If songs but no setlists, add message to add a new playlist
    // If setlists, show setlists by date/venue
}