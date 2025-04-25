package com.plcoding.bookpedia.book.presentation.book_list

import com.plcoding.bookpedia.book.domain.Book

// specify what actions the user can perform
sealed interface BookListActions {
    data class OnSearchQueryChange(val query: String): BookListActions
    data class OnBookClick(val book: Book): BookListActions
    data class OnTabSelected(val index: Int): BookListActions
}

// Note: the UI is not responsible for deciding what should happen after a user action,
// but the ViewModel is responsible for that since only the ViewModel can change the state.
// The UI behaves based on the change in the state.