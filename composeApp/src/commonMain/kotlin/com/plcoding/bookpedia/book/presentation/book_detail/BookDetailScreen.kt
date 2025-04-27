@file:OptIn(ExperimentalLayoutApi::class)

package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.description_unavailable
import cmp_bookpedia.composeapp.generated.resources.languages
import cmp_bookpedia.composeapp.generated.resources.pages
import cmp_bookpedia.composeapp.generated.resources.rating
import cmp_bookpedia.composeapp.generated.resources.synopsis
import com.plcoding.bookpedia.book.presentation.book_detail.components.BlurredImageBackground
import com.plcoding.bookpedia.book.presentation.book_detail.components.BookChip
import com.plcoding.bookpedia.book.presentation.book_detail.components.ChipSize
import com.plcoding.bookpedia.book.presentation.book_detail.components.TitleContent
import com.plcoding.bookpedia.core.presentation.SandYellow
import com.plcoding.bookpedia.core.presentation.SharedContentKeys
import org.jetbrains.compose.resources.stringResource
import kotlin.math.round

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailScreenRoot(
    viewModel: BookDetailViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit, // to pop this screen out of the navbackstack
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookDetailScreen(
        state = state,
        onAction = {action ->

            // intercept the communication to viewmodel and handle actions
            // that are outside of view model's responsibility here
            when(action) {
                is BookDetailAction.OnBackClick -> onBackClick()
                else -> Unit
            }

            // let viewmodel handle actions that are within its responsibilities
            viewModel.onAction(action)
        },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
private fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    BlurredImageBackground(
        bookId = state.book?.id,
        imageUrl = state.book?.imageUrl,
        isFavorite = state.isFavorite,
        onFavoriteClick = {
            onAction(BookDetailAction.OnFavoriteClick)
        },
        onBackClick = {
            onAction(BookDetailAction.OnBackClick)
        },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = Modifier.fillMaxSize(),
    ) {
        with(sharedTransitionScope) {
            if (state.book != null) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .fillMaxWidth()
                        .padding(
                            vertical = 16.dp,
                            horizontal = 24.dp
                        )
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.book.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = state.book.authors.joinToString(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Row(modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState("${SharedContentKeys.BOOK_RATING}/${state.book.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> tween(1000) }
                        )
                        .padding(vertical = 8.dp)
                        ,
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // add 16 dp space between items
                    ) {
                        // if there's rating
                        state.book.averageRating?.let { rating ->
                            TitleContent(
                                title = stringResource(Res.string.rating)
                            ) {
                                BookChip() {
                                    Text(
                                        text = "${round(rating * 10) / 10.0}"
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = SandYellow
                                    )
                                }
                            }
                        }
                        // if there're pages
                        state.book.numPages?.let { numPages ->
                            TitleContent(
                                title = stringResource(Res.string.pages)
                            ) {
                                BookChip {
                                    Text(
                                        text = "$numPages",
                                    )
                                }
                            }
                        }
                    }

                    // if there're languages
                    if (state.book.languages.isNotEmpty()) {
                        TitleContent(
                            title = stringResource(Res.string.languages),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.Center, // center rows that don't have enough items to fill it
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            ) {
                                state.book.languages.forEach { lang ->
                                    BookChip(
                                        size = ChipSize.SMALL,
                                        modifier = Modifier.padding(2.dp) // separate items in flow row
                                    ) {
                                        Text(
                                            text = lang.uppercase(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = stringResource(Res.string.synopsis),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.Start) // left align to column
                            .fillMaxWidth()
                            .padding(
                                top = 24.dp,
                                bottom = 8.dp
                            )
                    )

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Text(
                            text = if (state.book.description.isNullOrBlank()) {
                                stringResource(Res.string.description_unavailable)
                            } else {
                                state.book.description
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify, // max width
                            color = if (state.book.description.isNullOrBlank()) {
                                Color.Black.copy(alpha = 0.4f)
                            } else {
                                Color.Black
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        )
                    }



                }
            }
        }
    }
}






