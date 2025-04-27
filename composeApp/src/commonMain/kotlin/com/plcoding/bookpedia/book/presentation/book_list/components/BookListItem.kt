package com.plcoding.bookpedia.book.presentation.book_list.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.book_error_2
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.core.presentation.LightBlue
import com.plcoding.bookpedia.core.presentation.PulseAnimation
import com.plcoding.bookpedia.core.presentation.SandYellow
import com.plcoding.bookpedia.core.presentation.SharedContentKeys
import org.jetbrains.compose.resources.painterResource
import kotlin.math.round

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationSpecApi::class)
@Composable
fun BookListItem(
    book: Book,
    onClick: () -> Unit, // for navigating upon clicking
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        // Transformation for bounds of shared elements to customize how
        //  the shared element transition animation runs
        val bookCoverBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
            keyframes {
                durationMillis = 1000
                initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                targetBounds at 1000
            }
        }

        Surface(
            shape = RoundedCornerShape(32.dp),
            modifier = modifier
                .clickable { onClick() },
            color = LightBlue.copy(alpha = 0.2f)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    // use the min height that would need to fit its children, no more that that
                    // This allows children to use fillMaxHeight without taking up the entire screen height
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Use Box here to switch between book cover or loading animation
                Box(
                    modifier = Modifier
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var imageLoadResult by remember {
                        // This Result can be null
                        mutableStateOf<Result<Painter>?>(null) // Painter is sth we can use to paint or draw image
                    }

                    val painter = rememberAsyncImagePainter( // from coil
                        // model can be a bit map or URL, etc. and coil will figure out how to load the image
                        model = book.imageUrl,
                        onSuccess = { // override lambda that triggers when image is successfully loaded
                            // our data API sometimes provide image with no dimensions so we need to check for that
                            imageLoadResult = if (it.painter.intrinsicSize.width > 1 && it.painter.intrinsicSize.height > 1) {
                                Result.success(it.painter)
                            } else {
                                Result.failure(Exception("Inavlid image size"))
                            }
                        },
                        onError = {
                            it.result.throwable.printStackTrace()
                            imageLoadResult = Result.failure(it.result.throwable)
                        }
                    )

                    // observe the painter state to animate
                    val painterState by painter.state.collectAsStateWithLifecycle()
                    // use it to check when switch from loading to successful, we anime the book cover
                    val transition by animateFloatAsState(
                        targetValue = if (painterState is AsyncImagePainter.State.Success) {
                            1f
                        } else {
                            0f
                        },
                        animationSpec = tween(durationMillis = 800)
                    )

                    when(val result = imageLoadResult) {
                        null -> PulseAnimation(
                            modifier = Modifier.size(60.dp)
                        )
                        else -> {
                            Image(
                                painter = if (result.isSuccess) painter else {
                                    // create a painter using painterResource
                                    painterResource(Res.drawable.book_error_2)
                                },
                                contentDescription = book.title,
                                contentScale = if (result.isSuccess) {
                                    ContentScale.Crop // center crop the image when it does not fit our dimensions
                                } else {
                                    ContentScale.Fit // fit the book_error_2 default img
                                },
                                modifier = Modifier
                                    .aspectRatio(
                                        ratio = 0.65f, // force all images to have the same aspect ratio
                                        matchHeightConstraintsFirst = true // try to maximize the height first and then width bc book covers are vertical
                                    )
                                    .graphicsLayer {
                                        // we want the book cover rotates from the inside of the screen towards us
                                        // we rotate around the x-axis (horizontal axis)
                                        rotationX = (1f - transition) * 30f
                                        // initially, the book starts at 30 deg towards the inside of the screen
                                        // we bring it to 0 deg over time
                                        val scale = 0.8f + (0.2f * transition) // start at 80%, and add the remaining 20% over time
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "${SharedContentKeys.BOOK_IMAGE}/${book.id}"), // let compose know which composable transitions to which composable
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = bookCoverBoundsTransform
                                    )
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), // put other elements in before letting this take up the leftover space
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    book.authors.firstOrNull()?.let { authorName ->
                        Text(
                            text = authorName,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    book.averageRating?.let { avgRating ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState("${SharedContentKeys.BOOK_RATING}/${book.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(1000) }
                                )
                        ) {
                            Text(
                                text = "${round(avgRating * 10) / 10.0}", // round to 1 decimal place
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SandYellow
                            )
                        }

                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                )
            }
        }
    }

}