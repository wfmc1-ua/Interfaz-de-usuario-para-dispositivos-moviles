package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.os.Parcelable
import android.graphics.Bitmap
import android.os.Parcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    var title: String = "",
    var director: String = "",
    var year: Int = 0,
    var genre: Genre = Genre.Action,
    var format: Format = Format.DVD,
    var imdbUrl: String = "",
    var comments: String = "",

    var imageResId: Int = 0,
    var imageUrl: String? = null,
    var image: Bitmap? = null,


    ) : Parcelable {

    // Same order as the genres array in arrays.xml, so ordinal is the position in the dropdown
    enum class Genre { Action, Drama, Comedy, Horror, SciFi }

    // Same order as the formats array in arrays.xml
    enum class Format { DVD, BluRay, Digital }
}
