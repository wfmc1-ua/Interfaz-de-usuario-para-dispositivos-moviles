package es.ua.eps.filmoteca

import android.graphics.Bitmap
import android.os.Parcelable
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

//-------------------------------------
// Sample data, until the films come from a real source
fun sampleFilms() = listOf(
    Film(
        title = "El quinto elemento",
        director = "Luc Besson",
        year = 1997,
        genre = Film.Genre.SciFi,
        format = Film.Format.BluRay,
        imdbUrl = "https://www.imdb.com/title/tt0119116/",
        comments = "Edición de coleccionista",
        imageResId = R.drawable.poster_el_quinto_elemento,
    ),
    Film(
        title = "El padrino",
        director = "Francis Ford Coppola",
        year = 1972,
        genre = Film.Genre.Drama,
        format = Film.Format.DVD,
        imdbUrl = "https://www.imdb.com/title/tt0068646/",
        comments = "Pendiente de ver la versión restaurada",
    ),
)

//-------------------------------------
// Until the next practice, the film is chosen by its title among the sample films
fun filmWithTitle(title: String?) =
    sampleFilms().firstOrNull { it.title == title } ?: sampleFilms().first()
