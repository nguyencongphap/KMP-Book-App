package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabase

// In addition to BookDatabaseConstructor, we need this to create our db
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<FavoriteBookDatabase> {
       // say that we want to save this db file on iOS
       // doesn't have Mac to have NSFileManager
    }
}