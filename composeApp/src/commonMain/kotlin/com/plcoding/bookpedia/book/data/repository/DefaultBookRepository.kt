package com.plcoding.bookpedia.book.data.repository

import androidx.sqlite.SQLiteException
import com.plcoding.bookpedia.book.data.database.FavoriteBookDao
import com.plcoding.bookpedia.book.data.mappers.toBook
import com.plcoding.bookpedia.book.data.mappers.toBookEntity
import com.plcoding.bookpedia.book.data.network.RemoteBookDataSource
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Repository is the one stop for domain layer to access different data sources
// It's a combination of different data sources
class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource, // depends on abstraction instead of impl of data source
    private val favoriteBookDao: FavoriteBookDao
): BookRepository {

    // for simplicity, we don't cache and do offline first for searching. We just solely rely on http client
    override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
        return remoteBookDataSource.searchBooks(query)
            .map { respDto ->
                respDto.results.map { bookDto ->
                    bookDto.toBook()
                }
            }
    }

    override suspend fun getBookDescription(bookId: String): Result<String?, DataError> {
        // offline first
        // repo's job is to coordinate between multiple data sources
        val localResult = favoriteBookDao.getFavoriteBook(bookId)

        return if (localResult == null) {
            // if doesn't have data of it in local db, then we request the remote api for the result
            remoteBookDataSource.getBookDetails(bookId)
            .map { bookWorkDto ->
                bookWorkDto.description
            }
        } else {
            Result.Success(localResult.description)
        }
    }

    override fun getFavoriteBooks(): Flow<List<Book>> {
        return favoriteBookDao.getFavoriteBooks()
            .map { bookEntities ->
                bookEntities.map { entity ->
                    entity.toBook()
                }
            }
    }

    override fun isBookFavorite(id: String): Flow<Boolean> {
        return favoriteBookDao
            .getFavoriteBooks()
            .map { bookEntities ->
                bookEntities.any { entity -> entity.id == id}
            }
    }

    override suspend fun markAsFavorite(book: Book): EmptyResult<DataError.Local> {
        return try {
            favoriteBookDao
                .upsert(book.toBookEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    }

    override suspend fun deleteFromFavorites(id: String) {
        favoriteBookDao.deleteFavoriteBook(id)
    }

}