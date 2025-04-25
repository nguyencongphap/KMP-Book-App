package com.plcoding.bookpedia.core.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun PulseAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    // extract progress from transition.
    // We want to fill the circle from 0% to 100%
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        // how animation works
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // one pulse takes 1 sec
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .graphicsLayer { // typical modifier used for animation. With this, we can change translation and other things of the composable
                // This graphicsLayer lambda doesn't trigger recomposition when we use sth that updates multiple times like "progress" above
                // When we call modifier.scale(progress), progress is considered as a real state
                // because it's not used in a lambda. Therefore, it triggers a lot of recompositions
                // Instead of doing that, we want to change the scale here in this graphicsLayer lambda

                scaleX = progress
                scaleY = progress
                // The more we move the pulse circle outside, the more we want to fade it out
                alpha = 1f - progress // when progress is 0, alpha will be 1 and vice versa

            }
            .border(
                width = 5.dp,
                color = SandYellow,
                shape = CircleShape
            )
    )
}