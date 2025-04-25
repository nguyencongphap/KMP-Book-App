package com.plcoding.bookpedia.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.plcoding.bookpedia.book.presentation.SelectedBookViewModel
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailAction
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailScreenRoot
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailViewModel
import com.plcoding.bookpedia.book.presentation.book_list.BookListScreenRoot
import com.plcoding.bookpedia.book.presentation.book_list.BookListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
@Preview
fun App() {
    MaterialTheme { // wrap everything with this to get theming values that we can change by configuring theme

        // This the perfect place to set up navigation
        val navController = rememberNavController() // this the central instance that we use to jump from one screen to another

        NavHost(
            // this is where we specify the initial screen and other screens that we can get to
            navController = navController,
            startDestination = Route.BookGraph
        ) {

            // we can share view model between two screens by taking the view model and scope it to the entry of the
            // navigation graph because navigation graph will stay alive as long as any screen
            // in the nav graph stay alive

            // define the navigation graph
            // pass the class that makes up the graph
            navigation<Route.BookGraph>(
                // a graph needs a start destination
                startDestination = Route.BookList
            ) {
                // we can now define our actual screen destinations with composable blocks
                composable<Route.BookList>(
                    // we never navigate to this screen, we only go back to it using back button
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { // for when we go back to this screen by popping the navbackstack
                        slideInHorizontally()
                    }
                ) {
                    // check if there is a view model implementation of this type, if yes, koin will retrieve it
                    // and properly bind it to this screen
                    val viewModel = koinViewModel<BookListViewModel>()

                    // use our util fun below to get the shared view model set up
                    val selectedBookViewModel =
                        it.sharedKoinViewModel<SelectedBookViewModel>(navController) // "it" is the BackStackEntry

                    // Upon going back to this screen, we want to clear selectedBook state
                    LaunchedEffect(true) { // true means this effect will trigger whenever this screen appears somehow (either by first launch or nav back)
                        selectedBookViewModel.onSelectBook(null)
                    }

                    BookListScreenRoot(
                        // the screen just needs the instance of the view model
                        // the view model will receive many different dependencies
                        viewModel = viewModel,
                        onBookClick = { book ->
                            selectedBookViewModel.onSelectBook(book)
                            navController.navigate(
                                Route.BookDetail(book.id) // send book.id when navigating to BookDetail
                            )
                        },
                        modifier = Modifier
                    )
                }
                composable<Route.BookDetail>(
                    enterTransition = { slideInHorizontally { initialOffset ->
                        initialOffset // we anime using the full width, no offset
                    } },
                    exitTransition =  { slideOutHorizontally { initialOffset ->
                        initialOffset
                    } }
                ) { it ->
//                    val args = it.toRoute<Route.BookDetail>() // access args that were sent in navigating to this screen like useParams

                    val viewModel = koinViewModel<BookDetailViewModel>()

                    // This view model instance will be the same as the selectedBookViewModel we set up in composable<Route.BookList> above.
                    // They are one same shared view model.
                    val selectedBookViewModel =
                        it.sharedKoinViewModel<SelectedBookViewModel>(navController) // "it" is the BackStackEntry

                    // TODO: DEL LATER
                    val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()
                    // Sync the selected book state of shared view model with that of private view model
                    LaunchedEffect(selectedBook) {
                        selectedBook?.let { // declarative null check
                            viewModel.onAction(BookDetailAction.OnSelectedBookChange(it))
                        }
                    }

                    BookDetailScreenRoot(
                        viewModel = viewModel,
                        modifier = Modifier,
                        onBackClick = {
                            navController.navigate(
                                Route.BookList
                            )
                        }
                    )
                }
            }
        }


    }

}

// util extension function for sharing view model
@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T { // return the view model
    // the graph is the backstack entry

    // fetch route of the parent nav graph
    // destination is a screen that we're on
    val navGraphRoute = destination.parent?.route ?: return  koinViewModel<T>() // return view model if parent.route doesn't exist
    // get navbackstackentry of our parent navigation graph
    val parentEntry = remember(this) { // this is NavBackStackEntry because this is the extension function of NavBackStackEntry
        navController.getBackStackEntry(navGraphRoute)
    }

    return koinViewModel(
        // explitcitly scope this viewmodel given to this function to the nav graph's NavBackStackEntry
        // instead of leaving scoped in one single screen's NavBackStackEntry
        viewModelStoreOwner = parentEntry
    )
}












