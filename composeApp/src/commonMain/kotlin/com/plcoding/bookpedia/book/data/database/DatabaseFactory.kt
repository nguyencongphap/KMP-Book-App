package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabase

// In addition to BookDatabaseConstructor, we need this to create our db
expect class DatabaseFactory {

    fun create(): RoomDatabase.Builder<FavoriteBookDatabase>

}