package com.projects.aware.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.projects.aware.R
import com.projects.aware.main.settings.SettingsViewModel

// In your SettingsScreen.kt or a new file for password management

// --- ViewModel (SettingsViewModel or a dedicated PasswordViewModel) ---
// - fun createPin(pin: String)
// - fun verifyPin(pin: String): Boolean
// - fun changePin(oldPin: String, newPin: String)
// - fun removePin(currentPin: String)
// - val isPinSet: StateFlow<Boolean>
// - val pinError: StateFlow<String?> // For displaying errors during PIN entry
// - val pinLockoutTime: StateFlow<Long?> // For attempt limits


enum class PasswordDialogType {
    NONE, SET, CHANGE, REMOVE
}

@Composable
fun DialogsManager(
    modifier: Modifier = Modifier,
    currentDialogType: PasswordDialogType,
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    when (currentDialogType) {
        PasswordDialogType.SET -> PasswordInputDialog(
            title = stringResource(R.string.set_PIN),
            confirmLabel = stringResource(R.string.save),
            onConfirm = { new, _ ->
                viewModel.createPin(new,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            R.string.PIN_set_successfully,
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss()
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            R.string.PIN_must_be_4_digits,
                            Toast.LENGTH_SHORT
                        ).show()
                    })


            },
            onDismiss = onDismiss
        )

        PasswordDialogType.CHANGE -> PasswordInputDialog(
            title = stringResource(R.string.change_PIN),
            confirmLabel = stringResource(R.string.update),
            requireOldPassword = true,
            onConfirm = { new, old ->
                viewModel.changePin(old ?: "", new, onSuccess = {
                    Toast.makeText(
                        context,
                        R.string.PIN_updated_successfully,
                        Toast.LENGTH_SHORT
                    ).show()
                    onDismiss()
                }, onFailure = {
                    Toast.makeText(
                        context,
                        R.string.incorrect_PIN,
                        Toast.LENGTH_SHORT
                    ).show()
                })


            },
            onDismiss = onDismiss
        )

        PasswordDialogType.REMOVE -> RemovePasswordDialog(
            onConfirm = { pin ->
                viewModel.removePin(pin,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            R.string.PIN_removed_successfully,
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss()
                    },
                    onFailure = {
                        Toast.makeText(
                            context,
                            R.string.incorrect_PIN,
                            Toast.LENGTH_SHORT
                        ).show()
                    })

            },
            onDismiss = onDismiss
        )

        else -> {}
    }

}

@Composable
fun PasswordInputDialog(
    title: String,
    confirmLabel: String,
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit,
    requireOldPassword: Boolean = false
) {
    var newPassword by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Row {
                if (requireOldPassword) {
                    CustomPinTextField(
                        modifier = Modifier.weight(1f),
                        pin = oldPassword,
                        label = stringResource(R.string.old_pin),
                        onPinEntered = { oldPassword = it },
                        imeAction = ImeAction.Next
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                CustomPinTextField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.new_pin),
                    pin = newPassword,
                    imeAction = ImeAction.Done,
                    onPinEntered = { newPassword = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(newPassword, if (requireOldPassword) oldPassword else null)
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun RemovePasswordDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.remove_PIN)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_your_current_PIN_to_remove_password_protection))
                Spacer(modifier = Modifier.height(8.dp))
                CustomPinTextField(
                    modifier = Modifier.fillMaxWidth(),
                    pin = pin,
                    label = stringResource(R.string.PIN),
                    onPinEntered = { pin = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pin) }) {
                Text(stringResource(R.string.remove), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}


@Composable
fun CustomPinTextField(
    modifier: Modifier = Modifier,
    pin: String,
    label: String = "",
    imeAction: ImeAction = ImeAction.Done,
    onPinEntered: (String) -> Unit
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) onPinEntered(it) },
            label = { Text(label) },
            visualTransformation = PasswordVisualTransformation('•'),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.NumberPassword,
                imeAction = imeAction
            ),
            modifier = Modifier
                .width(150.dp)
                .padding(16.dp),
            textStyle = TextStyle(
                letterSpacing = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            placeholder = {
                Text(
                    "_  _  _  _",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

