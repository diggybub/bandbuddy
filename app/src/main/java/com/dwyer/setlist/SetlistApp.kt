package com.dwyer.setlist

import android.app.Application
import com.dwyer.setlist.SetlistApp

class SetlistApp : Application() {
    override fun onCreate() {
        instance = this@SetlistApp
        super.onCreate()
    }

    companion object {
        var instance: SetlistApp? = null
            private set
    }
}