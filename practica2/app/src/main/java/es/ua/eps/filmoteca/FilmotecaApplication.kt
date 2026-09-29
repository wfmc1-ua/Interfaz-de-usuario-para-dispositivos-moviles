package es.ua.eps.filmoteca

import android.app.Application
import com.google.android.material.color.ColorContrast
import com.google.android.material.color.ColorContrastOptions

class FilmotecaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // On Android 14+ the user can raise the color contrast in the accessibility settings.
        // This applies the matching theme overlay from theme_overlays.xml to every activity, and
        // recreates the open activities when the setting changes
        val options = ColorContrastOptions.Builder()
            .setMediumContrastThemeOverlay(R.style.ThemeOverlay_AppTheme_MediumContrast)
            .setHighContrastThemeOverlay(R.style.ThemeOverlay_AppTheme_HighContrast)
            .build()
        ColorContrast.applyToActivitiesIfAvailable(this, options)
    }
}
