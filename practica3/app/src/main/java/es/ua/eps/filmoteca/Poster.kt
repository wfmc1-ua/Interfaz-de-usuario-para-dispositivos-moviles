package es.ua.eps.filmoteca

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlin.math.roundToInt

//-------------------------------------
// The poster is the chosen image if there is one, and otherwise the drawable of the sample film
fun ImageView.showPoster(image: Bitmap?, imageResId: Int) {
    when {
        image != null -> setImageBitmap(image)
        imageResId != 0 -> setImageResource(imageResId)
        else -> setImageDrawable(null)
    }
}

@Composable
fun PosterImage(image: Bitmap?, imageResId: Int, description: String, modifier: Modifier) {
    when {
        image != null -> Image(
            bitmap = image.asImageBitmap(),
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
        imageResId != 0 -> Image(
            painter = painterResource(imageResId),
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    }
}

//-------------------------------------
// A photo from the gallery is too big for an intent (about 1 MB), so a small copy is loaded:
// first only the size (inJustDecodeBounds), then reduced by a power of 2 (inSampleSize), and
// finally scaled to the exact size. With 400 px and RGB_565 (2 bytes per pixel) it is about 240 KB
fun Context.loadScaledBitmap(uri: Uri, maxSize: Int = 400): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sampleSize = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= maxSize) {
        sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.RGB_565
    }
    val decoded = contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    } ?: return null

    val scale = maxSize.toFloat() / maxOf(decoded.width, decoded.height)
    if (scale >= 1f) return decoded
    val scaled = Bitmap.createScaledBitmap(
        decoded,
        (decoded.width * scale).roundToInt().coerceAtLeast(1),
        (decoded.height * scale).roundToInt().coerceAtLeast(1),
        true,
    )
    if (scaled !== decoded) decoded.recycle()
    return scaled
}
