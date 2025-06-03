package com.dwyer.bandbuddy.di

import app.cash.sqldelight.db.SqlDriver
import com.dwyer.bandbuddy.data.DatabaseFactory
import com.dwyer.bandbuddy.data.SongRepository
import com.dwyer.bandbuddy.data.SetlistRepository
import com.dwyer.bandbuddy.data.SetlistItemRepository
import org.koin.dsl.module

fun createSqlDelightModule(sqlDriver: SqlDriver) = module {
    single<SqlDriver> { sqlDriver }
    single<SongRepository> { DatabaseFactory.createSongRepository(get()) }
    single<SetlistRepository> { DatabaseFactory.createSetlistRepository(get()) }
    single<SetlistItemRepository> { DatabaseFactory.createSetlistItemRepository(get()) }
}

// Alternative modules list for SQLDelight
fun createSqlDelightModules(sqlDriver: SqlDriver) = listOf(
    createSqlDelightModule(sqlDriver),
    useCaseModule
)