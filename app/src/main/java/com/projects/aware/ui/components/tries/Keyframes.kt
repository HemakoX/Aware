package com.projects.aware.ui.components.tries

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun BouncingBall() {
    val animationState = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Automatically restart animation (infinite loop)
    LaunchedEffect(Unit) {
        while (true) {
            animationState.value = !animationState.value
            delay(2000) // Duration of one animation cycle
        }
    }

    // Using Transition API for synchronized animations
    val transition = updateTransition(targetState = animationState.value, label = "SynchronizedTransition")

    // Animating all properties in sync
    val offsetY by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = 2000
                0.dp at 0 with LinearEasing
                200.dp at 500 with FastOutSlowInEasing
                50.dp at 1000 with LinearEasing
                150.dp at 1500 with FastOutSlowInEasing
                0.dp at 2000
            }
        }, label = "offsetY"
    ) { if (it) 0.dp else 0.dp } // We maintain 0.dp for continuous animation

    val width by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = 2000
                50.dp at 0 with LinearEasing
                160.dp at 400 with LinearEasing
                200.dp at 500 with LinearEasing
                140.dp at 700 with LinearEasing
                100.dp at 1000 with LinearEasing
                50.dp at 2000
            }
        }, label = "width"
    ) { if (it) 50.dp else 50.dp }

    val height by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = 2000
                50.dp at 0 with LinearEasing
                40.dp at 400 with LinearEasing
                20.dp at 500 with LinearEasing
                30.dp at 700 with LinearEasing
                35.dp at 1000 with LinearEasing
                50.dp at 2000
            }
        }, label = "height"
    ) { if (it) 50.dp else 50.dp }



    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .offset(y = offsetY)
                .size(width = width, height = height)
                .background(Color.Red, shape = MaterialTheme.shapes.medium)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBouncingBall() {
    BouncingBall()
}
