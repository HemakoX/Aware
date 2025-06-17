package com.projects.aware.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier, onFinish: () -> Unit) {
    var showSubtitle by remember { mutableStateOf(false) }
    val textColor by animateColorAsState(
        targetValue = if (showSubtitle) MaterialTheme.colorScheme.primary else Color.LightGray,
        animationSpec = tween(700)
    )
    LaunchedEffect(Unit) {
        delay(700)
        showSubtitle = true
        delay(1000)
        onFinish()
    }
    val shimmerInstance = rememberShimmer(
        shimmerBounds = ShimmerBounds.View,
        theme = LocalShimmerTheme.current.copy(
            animationSpec = tween(700),
            shimmerWidth = 70.dp,
            shaderColors = if (showSubtitle) {
                listOf(
                    textColor,
                    textColor,
                    textColor
                )
            } else {
                LocalShimmerTheme.current.shaderColors
            }
        )
    )
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(true) {
                Text(
                    text = "Aware",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.shimmer(shimmerInstance),
                    color = textColor,
                )
            }
            AnimatedVisibility(showSubtitle) {
                if (showSubtitle) {
                    Spacer(Modifier.padding(vertical = 10.dp))
                    Text(
                        text = "Be Aware, Stay in Control",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}