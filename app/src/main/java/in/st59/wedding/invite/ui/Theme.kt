package `in`.st59.wedding.invite.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Wine = Color(0xFF3A0F19)
private val WineDeep = Color(0xFF2A0A12)
private val Gold = Color(0xFFC9A36A)
private val Cream = Color(0xFFF4E6D4)
private val Muted = Color(0xFFD7C4B0)

private val Colors = darkColorScheme(
    primary = Gold,
    onPrimary = WineDeep,
    secondary = Cream,
    onSecondary = WineDeep,
    background = WineDeep,
    onBackground = Cream,
    surface = Wine,
    onSurface = Cream,
    surfaceVariant = Color(0xFF4A1824),
    onSurfaceVariant = Muted,
    outline = Color(0x66C9A36A),
)

private val Type = Typography(
    headlineLarge = TextStyle(
        fontSize = 40.sp,
        lineHeight = 46.sp,
        fontWeight = FontWeight.SemiBold,
        color = Cream,
    ),
    headlineMedium = TextStyle(
        fontSize = 34.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.SemiBold,
        color = Cream,
    ),
    titleLarge = TextStyle(
        fontSize = 26.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Medium,
        color = Cream,
    ),
    titleMedium = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium,
        color = Cream,
    ),
    bodyLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = Cream,
    ),
    bodyMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        color = Cream,
    ),
    labelLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
    ),
)

@Composable
fun WeddingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colors,
        typography = Type,
        content = content,
    )
}
