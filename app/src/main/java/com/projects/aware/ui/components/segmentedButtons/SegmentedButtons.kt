package com.projects.aware.ui.components.segmentedButtons

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomButtonGroup(
    modifier: Modifier = Modifier,
    options: List<SegmentedButtonProp>,
    selectedOption: SegmentedButtonProp,
    onOptionSelected: (SegmentedButtonProp) -> Unit
) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(options, key = {it.label}) { option ->
                val weight = (1/options.size).toFloat()
                CustomSegmentedButton(
                    modifier = Modifier.fillMaxWidth(weight),
                    label = option.label,
                    icon = if (selectedOption == option) option.selectedIcon else option.unselectedIcon,
                    onClick = { onOptionSelected(option) },
                    selected = selectedOption == option,
                    index = options.indexOf(option),
                    optionsSize = options.size,
                )
            }
        }
    }

}

@Composable
fun CustomSegmentedButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    index: Int,
    optionsSize: Int,
    icon: ImageVector,
    @StringRes label: Int
) {

    val shape = when(index) {
        0 -> RoundedCornerShape(30.dp, 10.dp, 10.dp, 30.dp)
        optionsSize - 1 -> RoundedCornerShape(10.dp, 30.dp, 30.dp, 10.dp)
        else -> RoundedCornerShape(10.dp)
    }

    val containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface



    Card(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(selected) {
                Icon(
                    imageVector = icon,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    tint = contentColor
                )
            }
            Spacer(Modifier.padding(5.dp))
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}


data class SegmentedButtonProp(
    @StringRes val label: Int,
    val storeName: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)