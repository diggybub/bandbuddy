package com.dwyer.bandbuddy.android

import android.app.Application
import com.dwyer.bandbuddy.BandBuddyApp
import com.dwyer.bandbuddy.di.exportModule
import org.koin.core.context.loadKoinModules

class BandBuddyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin and the app
        BandBuddyApp.create()

        // Add Android-specific modules with context
        loadKoinModules(
            listOf(
                org.koin.dsl.module {
                    single { this@BandBuddyApplication as android.content.Context }
                },
                exportModule
            )
        )
    }
}
