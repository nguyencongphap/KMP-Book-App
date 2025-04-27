package com.plcoding.bookpedia.book.presentation.book_detail

import com.plcoding.bookpedia.book.domain.Book

data class BookDetailState(
    val isLoading: Boolean = true, // because we still need to fetch book description from a separate api endpoint
    val isFavorite: Boolean = false, // dictate whether to color the heart icon red or not
    val book: Book? = null, // we forward the state from the shared SelectedBook view model to this
)