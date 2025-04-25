package com.plcoding.bookpedia.book.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

// This class tells Room what our db consists of
// what are the DAOs, tables we work with
// db version and other configs

@Database(
    entities = [BookEntity::class], // specify entities/tables
    version = 1 // for db migration
)
@TypeConverters(
    StringListTypeConverter::class
) // add converter to convert lists into json strings
@ConstructedBy(BookDatabaseConstructor::class) // for platforms other than Android to work.
// By default, Room doesn't know which db is the BookDatabaseConstructor for so we have to link it
abstract class FavoriteBookDatabase: RoomDatabase() {
    abstract val favoriteBookDao: FavoriteBookDao // auto assigned by Room

    companion object {
        const val DB_NAME = "book.db"
    }
}