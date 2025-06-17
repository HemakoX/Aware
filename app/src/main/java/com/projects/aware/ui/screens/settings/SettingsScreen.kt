@file:OptIn(ExperimentalMaterial3Api::class)

package com.projects.aware.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.projects.aware.R
import com.projects.aware.data.repo.Language
import com.projects.aware.main.settings.AppTheme
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.main.settings.restartApp
import com.projects.aware.main.settings.toColorScheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    back: () -> Unit
) {
    BackHandler { back() }
    val context = LocalContext.current
    val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
    val language by settingsViewModel.language.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.displaySmall
                    )
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = back
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "navigate back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier,
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            item {
                ThemeSettings(
                    onThemeChange = {
                        settingsViewModel.updateTheme(it)
                    },
                    currentTheme = theme!!
                )
            }
            item {
                LanguageSettings(
                    onLanguageChange = {
                        settingsViewModel.updateLanguage(it)
                        restartApp(context = context)
                    },
                    currentLanguage = language
                )
            }
            item {
                ExpandableFeedbackCard(
                    settingsViewModel = settingsViewModel
                )
            }
        }

    }
}

@Composable
fun LanguageSettings(
    modifier: Modifier = Modifier,
    currentLanguage: String,
    onLanguageChange: (Language) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalDivider(thickness = 1.dp)
        LazyRow(
            modifier = modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(Language.entries, key = { it }) { lang ->
                LanguageCard(
                    language = lang,
                    selected = lang.value == currentLanguage,
                    onLanguageChange = {
                        onLanguageChange(lang)
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    language: Language,
    onLanguageChange: () -> Unit
) {
    Button(
        onClick = {
            if (!selected) {
                onLanguageChange()
            }
        },
        modifier = modifier,
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null,
        shape = RoundedCornerShape(20),
    ) {
        Text(text = language.name)
        AnimatedVisibility(selected) {
            Icon(
                Icons.Filled.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface
            )
        }
    }
}


@Composable
fun ThemeSettings(
    modifier: Modifier = Modifier,
    onThemeChange: (AppTheme) -> Unit,
    currentTheme: AppTheme
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            text = stringResource(R.string.theme),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalDivider(thickness = 1.dp)
        HorizontalThemePreviewRow(
            themes = AppTheme.entries.map { mapOf(it to it.toColorScheme()) },
            selectedIndex = AppTheme.entries.indexOf(currentTheme),
            onThemeSelected = { index -> onThemeChange(AppTheme.entries[index]) }
        )
    }
}

@Composable
fun HorizontalThemePreviewRow(
    themes: List<Map<AppTheme, ColorScheme>>,
    selectedIndex: Int,
    onThemeSelected: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        itemsIndexed(themes) { index, theme ->
            ThemePreviewCard(
                theme = theme.keys.first(),
                colorScheme = theme.values.first(),
                isSelected = index == selectedIndex,
                onClick = { onThemeSelected(index) }
            )
        }
    }
}


@Composable
fun ThemePreviewCard(
    theme: AppTheme,
    colorScheme: ColorScheme,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) colorScheme.primary else Color.Transparent

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .width(120.dp)
            .height(100.dp)
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Color palette preview row
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        colorScheme.primary,
                        colorScheme.secondary,
                        colorScheme.tertiary,
                        colorScheme.surface
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    0.5.dp,
                                    if (color.luminance() > 0.5f) Color.Black else Color.White,
                                    CircleShape
                                )
                        )
                    }
                }

                // Theme name
                Text(
                    text = themeName(theme),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Selected",
                tint = colorScheme.onPrimary,
                modifier = Modifier
                    .padding(4.dp)
                    .size(14.dp)
                    .background(colorScheme.primary, CircleShape)
                    .padding(2.dp)
            )
        }
    }
}

fun themeName(theme: AppTheme): String {
    return theme.name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar { it.uppercase() } }
}

@Composable
fun ExpandableFeedbackCard(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Always visible title
            Text(
                text = stringResource(R.string.help_us_improve) + " Aware",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            AnimatedContent(isExpanded) { expanded ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    if (!expanded) {
                        Button(
                            onClick = { isExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(stringResource(R.string.send_your_feedback))
                            // Collapsed state
                        }
                    } else {
                        TextField(
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            label = {
                                Text(stringResource(R.string.name))
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                textDirection = TextDirection.ContentOrRtl
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            supportingText = {
                                Text(stringResource(R.string.optional))
                            }
                        )
                        // Expanded state
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            placeholder = {
                                Text(stringResource(R.string.describe_your_experience))
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.medium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    isExpanded = false
                                    feedbackText = ""
                                    focusManager.clearFocus()
                                }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    scope.launch {

                                        // Simulate network request
                                        settingsViewModel.sendFeedback(
                                            message = feedbackText,
                                            name = name,
                                            onSuccess = {
                                                feedbackText = ""
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.thanks_for_your_feedback),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onError = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.please_check_your_internet_connection),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                        isExpanded = false
                                        feedbackText = ""
                                        focusManager.clearFocus()
                                    }
                                },
                                enabled = feedbackText.isNotBlank()
                            ) {
                                Text(stringResource(R.string.send))
                            }
                        }
                    }
                }
            }
        }
    }
}

