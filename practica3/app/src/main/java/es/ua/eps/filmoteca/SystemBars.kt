package es.ua.eps.filmoteca

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.MaterialColors

//-------------------------------------
// The status bar sits over the primary-colored app bar, not over the background, so its
// icons must contrast with colorPrimary
fun ComponentActivity.enableEdgeToEdgeWithAppBar() {
    val primary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, Color.BLACK)
    val statusBarStyle =
        if (MaterialColors.isColorLight(primary)) {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
        else {
            SystemBarStyle.dark(Color.TRANSPARENT)
        }
    enableEdgeToEdge(statusBarStyle = statusBarStyle)
}

//-------------------------------------
// Does for a view layout what Scaffold does in Compose. The system bars pad the app bar and the
// content, not the root, so the app bar background still reaches under the status bar and the
// display cutout
fun View.applySystemBarsPadding(appBar: View, content: View) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        // The keyboard is drawn over the window too. Padding the root shrinks the content above
        // it, and a ScrollView that shrinks scrolls the focused field back into view
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        view.setPadding(0, 0, 0, ime.bottom)
        appBar.setPadding(bars.left, bars.top, bars.right, 0)
        content.setPadding(bars.left, 0, bars.right, if (ime.bottom > 0) 0 else bars.bottom)
        insets
    }
}
