package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabaseConstructor

// Room needs this

// we do want to have this BookDatabaseConstructor in our shared code
// to construct our db, but we let each platform provides the
// implementation that works for them

// here we suppress because Room db generates the platform-specific implementations for us
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BookDatabaseConstructor: RoomDatabaseConstructor<FavoriteBookDatabase> {
    override fun initialize(): FavoriteBookDatabase
}