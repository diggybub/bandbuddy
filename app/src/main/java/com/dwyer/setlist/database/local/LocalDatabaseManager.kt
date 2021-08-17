package com.dwyer.setlist.database.local

import com.dwyer.setlist.database.DataSource
import com.dwyer.setlist.database.models.Setlist
import com.dwyer.setlist.database.models.Song
import com.squareup.sqldelight.runtime.rx3.asObservable
import com.squareup.sqldelight.runtime.rx3.mapToList
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Local datasource implementation
 * Maps database models to app models
 */
class LocalDatabaseManager : DataSource {
    override fun getAllSongsFromBank(): Observable<List<Song>> {
        return SQLDatabaseProvider.provideDatabase().songQueries
            .select_all(
                mapper = { _, artist, title, original -> Song(artist,title,original) }
            )
            .asObservable(Schedulers.io())
            .mapToList()
    }

    override fun getAllSetlists(): Observable<List<Setlist>> {
        return SQLDatabaseProvider.provideDatabase().setlistQueries
            .select_all(
                mapper = {date, artist, title, original -> Setlist(date,artist,title,original) }
            )
            .asObservable(Schedulers.io())
            .mapToList()
    }
}