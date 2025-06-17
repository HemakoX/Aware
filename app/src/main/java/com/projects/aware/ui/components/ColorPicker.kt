package com.projects.aware.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.projects.aware.R
import com.projects.aware.ui.screens.overlay.getAdjustedContrast
import kotlin.math.round

@OptIn(ExperimentalStdlibApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ColorPickDialog(
    modifier: Modifier = Modifier,
    initialColor: Color,
    onDismiss: () -> Unit,
    onSave: (color: Color) -> Unit,
    label: String,
) {
    val controller = rememberColorPickerController()
    Dialog(onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(10.dp)
            )
            ColorPick(
                onSave = { onSave(it) },
                initialColor = initialColor,
                controller = controller
            )
        }
    }
}


@Composable
fun ColorPick(
    onSave: (color: Color) -> Unit,
    controller: ColorPickerController,
    initialColor: Color,
    modifier: Modifier = Modifier,
) {
    var hexCode by remember { mutableStateOf("Color") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Text(
            stringResource(R.string.pick_a_color), modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge)
        HorizontalDivider(Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp))
        HsvColorPicker(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(10.dp),
            controller = controller,
            initialColor = initialColor,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                hexCode = colorEnvelope.hexCode
            },
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.padding(5.dp)
            ) {
                Text(
                    stringResource(R.string.brightness),
                    style = MaterialTheme.typography.titleLarge,
                )
                BrightnessSlider(
                    controller = controller,
                    wheelColor = controller.selectedColor.value.getAdjustedContrast(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20, 0, 20, 0))
                        .height(30.dp),
                    initialColor = initialColor
                )
            }
        }
        Spacer(Modifier.padding(5.dp))
        val animatedCardColor by animateColorAsState(
            targetValue = if (controller.selectedColor.value.alpha >= 0.3f) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
        )
        Card(
            colors = CardDefaults.cardColors(
               containerColor = animatedCardColor
            )
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.padding(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.translucency),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(round(controller.selectedColor.value.alpha * 100).toString() + "%")
                }
                AlphaSlider(
                    controller = controller,
                    wheelColor = controller.selectedColor.value.getAdjustedContrast(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20, 0, 20, 0))
                        .height(40.dp),
                    initialColor = initialColor
                )
                AnimatedVisibility(controller.selectedColor.value.alpha < 0.3f) {
                    Text(stringResource(R.string.translucency_must_be_over_50), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }


        OutlinedButton(
            enabled = controller.selectedColor.value.alpha >= 0.3f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = controller.selectedColor.value,
                contentColor = controller.selectedColor.value.getAdjustedContrast(),
                disabledContainerColor = Color.Gray
            ),
            onClick = { onSave(Color(controller.selectedColor.value.value)) }
        ) {
            Text(text = "Save")
        }
    }


}