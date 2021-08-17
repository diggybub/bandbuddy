package com.dwyer.setlist.database

import com.dwyer.setlist.database.models.Setlist
import com.dwyer.setlist.database.models.Song
import io.reactivex.rxjava3.core.Observable

/**
 * This class is used to abstract a datasource. There may be a remote or local source.
 */
interface DataSource {

    fun getAllSongsFromBank() : Observable<List<Song>>

    fun getAllSetlists() : Observable<List<Setlist>>

}