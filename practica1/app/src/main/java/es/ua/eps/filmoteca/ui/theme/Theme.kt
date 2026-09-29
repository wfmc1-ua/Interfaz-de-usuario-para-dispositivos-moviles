package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.ColorRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.content.ContextCompat

//-------------------------------------
// Material Theme Builder exports the palette twice, as colors.xml and as Color.kt. Reading the
// Compose colors from colors.xml keeps a single copy, so views and Compose cannot drift apart
private fun colorSchemeFromResources(
    context: Context,
    darkTheme: Boolean,
    contrast: Contrast
): ColorScheme {
    val modeContext = context.withNightMode(darkTheme)
    fun color(@ColorRes id: Int): Color {
        val variantId = contrast.variantOf(modeContext, id)
        return Color(ContextCompat.getColor(modeContext, variantId))
    }

    val base = if (darkTheme) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = color(R.color.md_theme_primary),
        onPrimary = color(R.color.md_theme_onPrimary),
        primaryContainer = color(R.color.md_theme_primaryContainer),
        onPrimaryContainer = color(R.color.md_theme_onPrimaryContainer),
        secondary = color(R.color.md_theme_secondary),
        onSecondary = color(R.color.md_theme_onSecondary),
        secondaryContainer = color(R.color.md_theme_secondaryContainer),
        onSecondaryContainer = color(R.color.md_theme_onSecondaryContainer),
        tertiary = color(R.color.md_theme_tertiary),
        onTertiary = color(R.color.md_theme_onTertiary),
        tertiaryContainer = color(R.color.md_theme_tertiaryContainer),
        onTertiaryContainer = color(R.color.md_theme_onTertiaryContainer),
        error = color(R.color.md_theme_error),
        onError = color(R.color.md_theme_onError),
        errorContainer = color(R.color.md_theme_errorContainer),
        onErrorContainer = color(R.color.md_theme_onErrorContainer),
        background = color(R.color.md_theme_background),
        onBackground = color(R.color.md_theme_onBackground),
        surface = color(R.color.md_theme_surface),
        onSurface = color(R.color.md_theme_onSurface),
        surfaceVariant = color(R.color.md_theme_surfaceVariant),
        onSurfaceVariant = color(R.color.md_theme_onSurfaceVariant),
        outline = color(R.color.md_theme_outline),
        outlineVariant = color(R.color.md_theme_outlineVariant),
        scrim = color(R.color.md_theme_scrim),
        inverseSurface = color(R.color.md_theme_inverseSurface),
        inverseOnSurface = color(R.color.md_theme_inverseOnSurface),
        inversePrimary = color(R.color.md_theme_inversePrimary),
        surfaceDim = color(R.color.md_theme_surfaceDim),
        surfaceBright = color(R.color.md_theme_surfaceBright),
        surfaceContainerLowest = color(R.color.md_theme_surfaceContainerLowest),
        surfaceContainerLow = color(R.color.md_theme_surfaceContainerLow),
        surfaceContainer = color(R.color.md_theme_surfaceContainer),
        surfaceContainerHigh = color(R.color.md_theme_surfaceContainerHigh),
        surfaceContainerHighest = color(R.color.md_theme_surfaceContainerHighest),
        primaryFixed = color(R.color.md_theme_primaryFixed),
        primaryFixedDim = color(R.color.md_theme_primaryFixedDim),
        onPrimaryFixed = color(R.color.md_theme_onPrimaryFixed),
        onPrimaryFixedVariant = color(R.color.md_theme_onPrimaryFixedVariant),
        secondaryFixed = color(R.color.md_theme_secondaryFixed),
        secondaryFixedDim = color(R.color.md_theme_secondaryFixedDim),
        onSecondaryFixed = color(R.color.md_theme_onSecondaryFixed),
        onSecondaryFixedVariant = color(R.color.md_theme_onSecondaryFixedVariant),
        tertiaryFixed = color(R.color.md_theme_tertiaryFixed),
        tertiaryFixedDim = color(R.color.md_theme_tertiaryFixedDim),
        onTertiaryFixed = color(R.color.md_theme_onTertiaryFixed),
        onTertiaryFixedVariant = color(R.color.md_theme_onTertiaryFixedVariant)
    )
}

//-------------------------------------
private enum class Contrast(val suffix: String) {
    Standard(""),
    Medium("_mediumContrast"),
    High("_highContrast");

    // Theme Builder repeats every color for each contrast level with a suffix in its name, so the
    // variant can only be found by name. Starting from the R.color id keeps the standard names
    // checked by the compiler
    @SuppressLint("DiscouragedApi")
    fun variantOf(context: Context, @ColorRes id: Int): Int {
        if (this == Standard) return id
        val name = context.resources.getResourceEntryName(id) + suffix
        return context.resources.getIdentifier(name, "color", context.packageName)
    }
}

//-------------------------------------
// Same thresholds that ColorContrast uses to pick the theme overlays for views
private fun Context.systemContrast(): Contrast {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return Contrast.Standard
    val level = getSystemService(UiModeManager::class.java)?.contrast ?: return Contrast.Standard
    return when {
        level >= 2f / 3f -> Contrast.High
        level >= 1f / 3f -> Contrast.Medium
        else -> Contrast.Standard
    }
}

//-------------------------------------
// Resources pick values-night from the system setting, so a theme forced to the other mode
// needs a context with that configuration
private fun Context.withNightMode(night: Boolean): Context {
    val config = resources.configuration
    val nightMask = Configuration.UI_MODE_NIGHT_MASK
    val isNight = (config.uiMode and nightMask) == Configuration.UI_MODE_NIGHT_YES
    if (isNight == night) return this

    val newConfig = Configuration(config)
    val nightValue = if (night) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
    newConfig.uiMode = (config.uiMode and nightMask.inv()) or nightValue
    return createConfigurationContext(newConfig)
}

//-------------------------------------
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // The preview does not run on a device, so there is no contrast setting to follow
    val contrast = if (LocalInspectionMode.current) Contrast.Standard else context.systemContrast()
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> remember(context, darkTheme, contrast) {
            colorSchemeFromResources(context, darkTheme, contrast)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
