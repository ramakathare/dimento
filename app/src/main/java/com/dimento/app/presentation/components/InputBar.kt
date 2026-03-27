package com.dimento.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dimento.app.presentation.theme.Primary
import com.dimento.app.presentation.theme.SurfaceContainerHigh

@Composable
fun InputBar(
    onSend: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = input,
            onValueChange = { input = it.take(200) },
            modifier = Modifier.weight(1f),
            placeholder = { androidx.compose.material3.Text("Write memory...") },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SurfaceContainerHigh,
                focusedContainerColor = SurfaceContainerHigh,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(
            onClick = {
                if (input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            },
            modifier = Modifier.background(Primary, RoundedCornerShape(999.dp))
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}
