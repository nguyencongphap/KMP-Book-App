package com.plcoding.bookpedia.book.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

// In addition to BookDatabaseConstructor, we need this to create our db
actual class DatabaseFactory(
    // for android we need reference to the context. This is required to get access to things like
    // the file system
    private val context:Context // will be injected in
) {
    actual fun create(): RoomDatabase.Builder<FavoriteBookDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(FavoriteBookDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }

}