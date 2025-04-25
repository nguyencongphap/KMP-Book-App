package com.plcoding.bookpedia.book.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote>
    suspend fun getBookDescription(bookId: String): Result<String?, DataError> // DataError instead of DataError.Remote
    // because we also fetch book description from local db

    fun getFavoriteBooks(): Flow<List<Book>> // returning a flow doesn't need to be async
    fun isBookFavorite(id: String): Flow<Boolean>
    // EmptyResult uses Unit which means we don't really have any data that we return, we just care about whether it's successful or not
    suspend fun markAsFavorite(book: Book): EmptyResult<DataError.Local> // db may be full so we might have error
    suspend fun deleteFromFavorites(id: String)
}