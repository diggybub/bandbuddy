package com.dwyer.setlist.database.models

import org.joda.time.DateTime

data class Setlist(val date: DateTime,val artist: String, val songTitle: String, val original:Boolean)

