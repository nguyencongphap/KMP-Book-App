package com.plcoding.bookpedia.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Presentation -> Domain <- Data
@OptIn(FlowPreview::class)
class BookListViewModel(
    private val bookRepository: BookRepository
): ViewModel() {

    private var cachedBooks = emptyList<Book>()
    private var searchJob: Job? = null // keep track of the current coroutine Job for the search
    private var observeFavoriteJob: Job? = null

    private val _state = MutableStateFlow(BookListState())
    val state = _state
        // doing this in "init" block of the view model is fine to, but
        // that leads to problems in testing
        .onStart {
            // as soon as we start listening to the flow using collect such as collectStateAsLifecycle
            // on the UI, then this will trigger
            if (cachedBooks.isEmpty()) { // for the first time when cachedBooks is empty
                // this onStart would be called again when we navigate back to the listing page
                // when flow collection (collectStateAsLifecycle) would start again, but cachedBooks
                // would not be empty after the first time onStart got executed
                observeSearchQuery()
            }
            observeFavoriteBooks()
        }
        .stateIn( // convert cold Flow to hot StateFlow
            viewModelScope,
            // while there active subscribers of our state, we will execute this whole _state flow chain
            // until 5 seconds after the last subscriber disappear
            SharingStarted.WhileSubscribed(5000L),
            _state.value // initial state
        )

    // expose a function where the UI can then pass a certain action to this ViewModel
    fun onAction(action: BookListActions) {
        when(action) {
            is BookListActions.OnBookClick -> {

            }
            is BookListActions.OnSearchQueryChange -> {
                // update state in a thread-safe manner. we can call this from multiple threads
                // on the same state object and won't run into race condition
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }
            is BookListActions.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
        }
    }

    // we need to observe favorite books in order to show them in Favorite list
    private fun observeFavoriteBooks() {
        // we should save this Flow into a Job variable because this function might be called multiple times by onStart.
        // This flows keeps on observing since the view model stays active when we navigate to
        // another screen. When we go back to this screen after more than 5s in our case,
        // the onStart will trigger again, it calls this function and results in adding another
        // observer. So, we need to save this into a var in order to cancel it later to make sure
        // we only have 1 observer. We don't need to do this if we use init {} instead of onStart
        observeFavoriteJob?.cancel()
        observeFavoriteJob = bookRepository
            .getFavoriteBooks()
            .onEach { // when something changes about our table (when there's an emission)
                favoriteBooks ->
                _state.update { it.copy(favoriteBooks = favoriteBooks) }
            }
            .launchIn(viewModelScope)
    }

    // listen to change in search query state and make a request for books
    private fun observeSearchQuery() {
        state
            .map { it.searchQuery } // map it so that we get string emissions whenever searchQuery changes
            .distinctUntilChanged() // ignore emissions in this flow chain when these emissions are the same
            .debounce(500L) // only trigger the search if we stop typing for 500 ms
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update { it.copy(
                            errorMessage = null,
                            searchResults = cachedBooks // show the last loaded books when query is empty
                        ) }
                    }
                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = searchBooks(query) // make this a separate function for readability
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    // return the Job returned by viewModelScope.launch
    // This allows us to cancel the currently running coroutine Job
    // and start a new one when we do a new search while the current search is still processing
    private fun searchBooks(query: String) =
        // search book is a suspend function so we need to do it within a coroutine scope
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true,
            ) }

            bookRepository
                .searchBooks(query)
                // This is where we call this so we check for success/error here using our util funcs
                .onSuccess { books ->
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = null,
                        searchResults = books
                    ) }
                }
                .onError { error ->
                    _state.update { it.copy(
                        searchResults = emptyList(),
                        isLoading = false,
                        errorMessage = error.toUiText()
                    ) }
                }
        }

}




















