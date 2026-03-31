package com.dimento.app.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.drawBehind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.random.Random

private const val BUBBLE_CACHE_VERSION = 1
private const val LIGHT_CACHE_FILE = "list_background_bubbles_light_v1.json"
private const val DARK_CACHE_FILE = "list_background_bubbles_dark_v1.json"

@Composable
fun AppBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val palette = remember(isDark) { backgroundPalette(isDark) }
    var bubbles by remember(isDark) { mutableStateOf<List<BubbleSpec>>(emptyList()) }

    LaunchedEffect(context, isDark) {
        bubbles = withContext(Dispatchers.IO) {
            loadOrCreateBubbles(context = context, isDark = isDark)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = palette.backgroundGradient,
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .drawBehind {
                val minDimension = size.minDimension
                val resolvedBubbles = if (bubbles.isEmpty()) {
                    generateBubbles(seed = if (isDark) 73_991 else 42_137, palette = palette)
                } else {
                    bubbles
                }

                resolvedBubbles.forEach { bubble ->
                    val radius = minDimension * bubble.radiusFraction
                    val center = Offset(
                        x = size.width * bubble.xFraction,
                        y = size.height * bubble.yFraction
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                bubble.innerColor.toComposeColor(),
                                bubble.outerColor.toComposeColor()
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }
            }
    )
}

private suspend fun loadOrCreateBubbles(context: Context, isDark: Boolean): List<BubbleSpec> {
    val cacheFile = context.cacheDir.resolve(if (isDark) DARK_CACHE_FILE else LIGHT_CACHE_FILE)

    runCatching {
        if (cacheFile.exists()) {
            val cached = cacheFile.readText().decodeBubbleSpecs()
            if (cached.isNotEmpty()) return cached
        }
    }

    val palette = backgroundPalette(isDark)
    val generated = generateBubbles(
        seed = Random.nextInt(),
        palette = palette
    )

    runCatching {
        cacheFile.writeText(generated.encodeToJson())
    }

    return generated
}

private fun generateBubbles(seed: Int, palette: BackgroundPalette): List<BubbleSpec> {
    val random = Random(seed)
    val bubbleCount = random.nextInt(from = 12, until = 19)

    return List(bubbleCount) {
        val centerAlpha = random.nextFloat().lerp(0.16f, 0.34f)
        val edgeAlpha = centerAlpha * random.nextFloat().lerp(0.10f, 0.28f)
        val color = palette.bubbleColors.random(random)

        BubbleSpec(
            xFraction = random.nextFloat().lerp(0.08f, 0.92f),
            yFraction = random.nextFloat().lerp(0.06f, 0.94f),
            radiusFraction = random.nextFloat().lerp(0.12f, 0.34f),
            innerColor = ColorSpec.from(color.copy(alpha = centerAlpha)),
            outerColor = ColorSpec.from(color.copy(alpha = edgeAlpha))
        )
    }
}

private fun backgroundPalette(isDark: Boolean): BackgroundPalette {
    return if (isDark) {
        BackgroundPalette(
            backgroundGradient = listOf(
                Color(0xFF09110D),
                Color(0xFF0F1915),
                Color(0xFF16231D)
            ),
            bubbleColors = listOf(
                Color(0xFF78D69B),
                Color(0xFF4DB6A5),
                Color(0xFFE8C97A),
                Color(0xFF8BC4FF),
                Color(0xFFB2F2A8)
            )
        )
    } else {
        BackgroundPalette(
            backgroundGradient = listOf(
                Color(0xFFF7F8F4),
                Color(0xFFEAF3EA),
                Color(0xFFE4EFEA)
            ),
            bubbleColors = listOf(
                Color(0xFF9FD7B5),
                Color(0xFFB8E3F4),
                Color(0xFFF4D7A1),
                Color(0xFFC9D5FF),
                Color(0xFFD8EDC4)
            )
        )
    }
}

private fun List<BubbleSpec>.encodeToJson(): String {
    val root = JSONObject()
    root.put("version", BUBBLE_CACHE_VERSION)
    root.put(
        "bubbles",
        JSONArray().apply {
            this@encodeToJson.forEach { bubble ->
                put(
                    JSONObject().apply {
                        put("xFraction", bubble.xFraction.toDouble())
                        put("yFraction", bubble.yFraction.toDouble())
                        put("radiusFraction", bubble.radiusFraction.toDouble())
                        put("innerColor", bubble.innerColor.toArgb())
                        put("outerColor", bubble.outerColor.toArgb())
                    }
                )
            }
        }
    )
    return root.toString()
}

private fun String.decodeBubbleSpecs(): List<BubbleSpec> {
    val root = JSONObject(this)
    if (root.optInt("version") != BUBBLE_CACHE_VERSION) return emptyList()

    val bubbles = root.optJSONArray("bubbles") ?: return emptyList()
    return buildList {
        for (index in 0 until bubbles.length()) {
            val item = bubbles.optJSONObject(index) ?: continue
            add(
                BubbleSpec(
                    xFraction = item.optDouble("xFraction").toFloat(),
                    yFraction = item.optDouble("yFraction").toFloat(),
                    radiusFraction = max(0.08f, item.optDouble("radiusFraction").toFloat()),
                    innerColor = ColorSpec.fromArgb(item.optInt("innerColor")),
                    outerColor = ColorSpec.fromArgb(item.optInt("outerColor"))
                )
            )
        }
    }
}

private fun Float.lerp(start: Float, end: Float): Float = start + (end - start) * this

private data class BackgroundPalette(
    val backgroundGradient: List<Color>,
    val bubbleColors: List<Color>
)

private data class BubbleSpec(
    val xFraction: Float,
    val yFraction: Float,
    val radiusFraction: Float,
    val innerColor: ColorSpec,
    val outerColor: ColorSpec
)

private data class ColorSpec(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float
) {
    fun toComposeColor(): Color = Color(red = red, green = green, blue = blue, alpha = alpha)

    fun toArgb(): Int = android.graphics.Color.argb(
        (alpha * 255f).toInt().coerceIn(0, 255),
        (red * 255f).toInt().coerceIn(0, 255),
        (green * 255f).toInt().coerceIn(0, 255),
        (blue * 255f).toInt().coerceIn(0, 255)
    )

    companion object {
        fun from(color: Color): ColorSpec = ColorSpec(
            red = color.red,
            green = color.green,
            blue = color.blue,
            alpha = color.alpha
        )

        fun fromArgb(argb: Int): ColorSpec = ColorSpec(
            red = android.graphics.Color.red(argb) / 255f,
            green = android.graphics.Color.green(argb) / 255f,
            blue = android.graphics.Color.blue(argb) / 255f,
            alpha = android.graphics.Color.alpha(argb) / 255f
        )
    }
}
