package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.book.data.dto.BookWorkDto
import com.plcoding.bookpedia.book.data.dto.SearchResponseDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result


// This interface is just a preference. It's not necessary to not violate clean architecture
// because the data layer can just use the implementation class of this interface which
// only has things in data layer

// interface for search endpoints
// create an abstraction of this data source
// if we would change HTTP library from ktor to something else in the future, then
// that would not be a problem because the only class we would need to replace is the
// implementation class of this interface
interface RemoteBookDataSource {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null
    ): Result<SearchResponseDto, DataError.Remote>

    suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote>
}
