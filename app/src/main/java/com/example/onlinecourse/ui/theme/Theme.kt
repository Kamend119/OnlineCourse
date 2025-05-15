package com.example.onlinecourse.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.onlinecourse.function.ThemePreferences

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryColor,
    secondary = DarkSecondaryColor,
    background = DarkBackgroundColor,
    surface = DarkSurfaceColor,
    onPrimary = DarkTextColor,
    onSecondary = DarkTextColor,
    onBackground = DarkTextColor,
    onSurface = DarkTextColor
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryColor,
    secondary = LightSecondaryColor,
    background = LightBackgroundColor,
    surface = LightSurfaceColor,
    onPrimary = LightTextColor,
    onSecondary = LightTextColor,
    onBackground = LightTextColor,
    onSurface = LightTextColor
)

@Composable
fun OnlineCursesTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val storedDarkTheme = themePreferences.getDarkModeState()

    val effectiveDarkTheme = darkTheme ?: storedDarkTheme

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (effectiveDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        effectiveDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
