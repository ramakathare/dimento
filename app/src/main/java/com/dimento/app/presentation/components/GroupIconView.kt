package com.dimento.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dimento.app.presentation.theme.getContrastColor
import com.dimento.app.presentation.theme.getGroupIconBackgroundColor
import java.io.File

@Composable
fun GroupIconView(
    name: String,
    icon: String?,
    size: Dp,
    fontSize: TextUnit = 16.sp
) {
    val backgroundColor = getGroupIconBackgroundColor(name)
    val contentColor = getContrastColor(backgroundColor)

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            if (icon.startsWith("vector:")) {
                val iconName = icon.removePrefix("vector:")
                val vector = getVectorIconByName(iconName)
                Icon(
                    imageVector = vector,
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.6f),
                    tint = contentColor
                )
            } else {
                val model = when {
                    icon.startsWith("/") -> File(icon)
                    icon.startsWith("file://") -> File(icon.removePrefix("file://"))
                    else -> icon
                }
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            val initials = remember(name) {
                if (name.isBlank()) "?"
                else {
                    val words = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
                    if (words.size >= 2) {
                        (words[0].take(1) + words[1].take(1)).uppercase()
                    } else if (words.isNotEmpty()) {
                        words[0].take(2).uppercase()
                    } else {
                        "?"
                    }
                }
            }
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getVectorIconByName(name: String): ImageVector {
    return when (name) {
        "work" -> Icons.Default.Work
        "office" -> Icons.Default.Apartment
        "machine" -> Icons.Default.PrecisionManufacturing
        "bike" -> Icons.AutoMirrored.Filled.DirectionsBike
        "car" -> Icons.Default.DirectionsCar
        "person" -> Icons.Default.Person
        "group" -> Icons.Default.Group
        "team" -> Icons.Default.Groups
        "building" -> Icons.Default.Apartment
        "real_estate" -> Icons.Default.RealEstateAgent
        "tools" -> Icons.Default.Build
        else -> Icons.Default.AddCircle
    }
}
