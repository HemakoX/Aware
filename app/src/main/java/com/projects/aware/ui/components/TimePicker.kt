package com.projects.aware.ui.components

import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.projects.aware.R

@Composable
fun TimePickerDialogButton(
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    TimePickerDialog(
        onDismiss = onDismiss,
        onConfirm = {
            onTimeSelected(it)
        }
    )
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var hours by remember { mutableIntStateOf(0) }
    var minutes by remember { mutableIntStateOf(0) }
    var seconds by remember { mutableIntStateOf(0) }

    AlertDialog(
        titleContentColor = Color.Black,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            Button(onClick = {
                val millis = (hours * 3600 + minutes * 60 + seconds) * 1000L
                onConfirm(millis)
                onDismiss()
            }) {
                Text(stringResource(R.string.set))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = {
            Text(
                stringResource(R.string.set_daily_limit),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberPickerView(
                    value = hours,
                    range = 0..23,
                    label = stringResource(R.string.hours)
                ) {
                    hours = it
                }
                NumberPickerView(
                    value = minutes,
                    range = 0..59,
                    label = stringResource(R.string.minutes)
                ) {
                    minutes = it
                }
                NumberPickerView(
                    value = seconds,
                    range = 0..59,
                    label = stringResource(R.string.seconds)
                ) {
                    seconds = it
                }
            }
        }
    )
}

@Composable
fun NumberPickerView(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val color = MaterialTheme.colorScheme.onSurface.toArgb()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AndroidView(
            factory = { context ->
                NumberPicker(context).apply {
                    minValue = range.first
                    maxValue = range.last
                    this.value = value
                    setOnValueChangedListener { _, _, newVal ->
                        onValueChange(newVal)
                    }
                    textColor = color
                }
            },
            update = {
                it.value = value
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label)
    }
}