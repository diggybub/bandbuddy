package com.dwyer.bandbuddy.di

import android.content.Context
import com.dwyer.bandbuddy.android.export.AndroidSetlistExporter
import com.dwyer.bandbuddy.export.SetlistExporter
import org.koin.dsl.module

val exportModule = module {
    single<SetlistExporter> { AndroidSetlistExporter(get<Context>()) }
}