package com.dwyer.setlist.database.local

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.dwyer.setlist.Database
import com.dwyer.setlist.SetlistApp
import com.dwyer.setlist.SetlistModel
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.joda.time.DateTime

val DB_NAME = "setlist.db"
val DB_VERSION = 1

object SQLDatabaseProvider {

    // Support SQLite open helper
    private val databaseHelper: SupportSQLiteOpenHelper by lazy {
        val config = SupportSQLiteOpenHelper
            .Configuration
            .builder(SetlistApp.instance!!)
            .name(DB_NAME)
            .callback(SetlistSQLCallback(DB_VERSION))
            .build()
        FrameworkSQLiteOpenHelperFactory().create(config)
    }

    // Create the database using provided adapters
    private val database: Database by lazy {
        Database(
            AndroidSqliteDriver(databaseHelper),
            SetlistModelAdapter = SetlistModel.Adapter(DATETIME_ADAPTER)
        )
    }

    // Provide the database
    fun provideDatabase() : Database {
        return database
    }
}

// Adapters for special types
val DATETIME_ADAPTER: ColumnAdapter<DateTime, Long> = object : ColumnAdapter<DateTime, Long> {
    override fun decode(databaseValue: Long): DateTime {
        return DateTime(databaseValue)
    }

    override fun encode(value: DateTime): Long {
        return value.millis
    }

}

class SetlistSQLCallback(dbVersion: Int) : SupportSQLiteOpenHelper.Callback(dbVersion) {
    override fun onCreate(db: SupportSQLiteDatabase) {
        val driver = AndroidSqliteDriver(db)
        Database.Schema.create(driver)
    }

    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val driver = AndroidSqliteDriver(db)
        Database.Schema.migrate(driver, oldVersion, newVersion)
    }

}
