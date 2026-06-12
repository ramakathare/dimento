package com.dimento.app.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimento.app.R
import com.dimento.app.core.DateFormats
import com.dimento.app.core.ValidationConstants

@Composable
fun EventComposerBar(
    text: String,
    selectedDateMillis: Long,
    hasCustomDateTime: Boolean,
    onTextChange: (String) -> Unit,
    onPickDateTime: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(), // Smoothly animates height changes when text grows or keyboard lifts
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Use safeDrawing to handle both system navigation bar and keyboard (IME) lifting.
                // This ensures the background (Surface) bleeds to the screen edge while
                // interactive content stays safely visible.
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ComposerTextBox(
                text = text,
                onTextChange = onTextChange,
                focusRequester = focusRequester,
                focusManager = focusManager,
                onSend = onSend
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateSelector(
                    selectedDateMillis = selectedDateMillis,
                    hasCustomDateTime = hasCustomDateTime,
                    onClick = onPickDateTime
                )

                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSend()
                        }
                        focusManager.clearFocus()
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(id = R.string.send_event),
                        tint = if (text.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposerTextBox(
    text: String,
    onTextChange: (String) -> Unit,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    onSend: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusRequester.requestFocus()
                }
        ) {
            BasicTextField(
                value = text,
                onValueChange = { onTextChange(it.take(ValidationConstants.MAX_EVENT_TEXT_LENGTH)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                minLines = 1,
                maxLines = 6, // Allows growing upward as text is added
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (text.isNotBlank()) {
                            onSend()
                        }
                        focusManager.clearFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (text.isBlank()) {
                            Text(
                                text = stringResource(id = R.string.write_memory_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun DateSelector(
    selectedDateMillis: Long,
    hasCustomDateTime: Boolean,
    onClick: () -> Unit
) {
    if (hasCustomDateTime) {
        DatePickerPill(
            label = DateFormats.eventDateTimeMillis(selectedDateMillis),
            onClick = onClick
        )
    } else {
        IconButton(
            onClick = onClick,
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(id = R.string.pick_date_time),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DatePickerPill(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraLarge)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = stringResource(id = R.string.pick_date_time),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
