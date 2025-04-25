package com.plcoding.bookpedia.book.presentation

import androidx.lifecycle.ViewModel
import com.plcoding.bookpedia.book.domain.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Usually we don't share view model between screens
// This view model is shared by Book List and Book Detail screen because the API doesn't
// serve much data in its BookDetail endpoint and we don't want to request the
// book search endpoint again for the same book upon landing on the Book Detail page.
// We don't want to store Book data in the local db either.
// We don't want to send big data as navigation argument either.

// We only keep shared state in this view model
class SelectedBookViewModel: ViewModel() {
    private val _selectedBook = MutableStateFlow<Book?>(null);
    val selectedBook = _selectedBook.asStateFlow()

    fun onSelectBook(book: Book?) {
        _selectedBook.value = book
    }
}