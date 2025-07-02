package com.projects.aware.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PinEntryField(
    pinLength: Int = 4,
    onPinEntered: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }

    val onNumberClick = { number: Int ->
        if (pin.length < pinLength) {
            pin += number.toString()
            if (pin.length == pinLength) {
                onPinEntered(pin)
            }
        }
    }

    val onBackspaceClick = {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // PIN Dots Indicator
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(pinLength) { index ->
                PinDot(isFilled = index < pin.length)
            }
        }

        // Custom numeric keypad
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row 1-3
            listOf(1..3, 4..6, 7..9).forEach { range ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    range.forEach { number ->
                        NumberButton(number, onNumberClick)
                    }
                }
            }

            // Bottom row (0 and backspace)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(modifier = Modifier.size(72.dp))
                NumberButton(0, onNumberClick)
                BackspaceButton(onBackspaceClick)
            }
        }
    }
}

@Composable
private fun PinDot(isFilled: Boolean) {
    val dotColor by animateColorAsState(
        targetValue = if (isFilled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFilled) Color.Transparent
        else MaterialTheme.colorScheme.outline
    )
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                color = dotColor,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            )
    )
}

@Composable
private fun NumberButton(number: Int, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable { onClick(number) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BackspaceButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Backspace,
            contentDescription = "Delete",
            modifier = Modifier.size(32.dp).clip(CircleShape),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}