package com.plcoding.bookpedia.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao // an interface specifying functions that we can use to interact with our db table
interface FavoriteBookDao {

    @Upsert // room will do everything for us
    suspend fun upsert(book: BookEntity)

    // we want a function that always automatically triggers
    // when we change something about this query,
    // when we change something about the favorite book table by adding a new book to it for example
    // We do that by using Flow. Room supports Flow.
    // Flow allows us to observe our List<BookEntity> and gives us the most up to date list
    @Query("SELECT * FROM BookEntity") // specify SQL we use to fetch data
    fun getFavoriteBooks(): Flow<List<BookEntity>>
        // Not an async fun because when we call this, it returns a Flow which doesn't do anything.
        // When launch the Flow in a co-routine scope and call "collect" on the Flow,
        // that's when Flow starts emitting values

    @Query("SELECT * FROM BookEntity WHERE id = :id")
    suspend fun getFavoriteBook(id: String): BookEntity?

    @Query("DELETE FROM BookEntity WHERE id = :id")
    suspend fun deleteFavoriteBook(id: String)
}