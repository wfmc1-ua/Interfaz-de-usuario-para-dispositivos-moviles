package es.ua.eps.filmoteca

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.core.os.BundleCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"
        private const val STATE_FILM = "film"
    }

    private lateinit var bindings: ActivityFilmDataBinding

    // State, so the Compose screen recomposes when it changes
    private var film by mutableStateOf(Film())

    // Receives the edited film. With Cancel the result is RESULT_CANCELED and nothing changes
    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            IntentCompat.getParcelableExtra(data, FilmEditActivity.EXTRA_FILM, Film::class.java)
                ?.let { showFilm(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableEdgeToEdgeWithAppBar()

        // After a rotation, the edited film is kept instead of going back to the sample one
        film = savedInstanceState?.let { BundleCompat.getParcelable(it, STATE_FILM, Film::class.java) }
            ?: filmWithTitle(intent.getStringExtra(EXTRA_FILM_TITLE))
        initUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_FILM, film)
    }

    private fun initUI() {
        when (GlobalMode) {
            Mode.Compose -> initUICompose()
            Mode.Bindings -> initUIBindings()
        }
    }

    private fun initUIBindings() {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            imdbButton.setOnClickListener { openImdb() }
            editFilmButton.setOnClickListener { editFilm() }
            backtoMainButton.setOnClickListener { goToMain() }

            mode.text = getString(R.string.using_mode) + " " + GlobalMode.name
        }
        showFilm(film)
    }

    // In Compose, changing the state is enough: the screen recomposes by itself
    private fun showFilm(newFilm: Film) {
        film = newFilm
        if (GlobalMode != Mode.Bindings) return

        with(bindings) {
            poster.showPoster(film.image, film.imageResId)
            poster.contentDescription = getString(R.string.poster_description, film.title)
            title.text = film.title
            director.text = film.director
            year.text = film.year.toString()

            val genres = resources.getStringArray(R.array.genres)
            val formats = resources.getStringArray(R.array.formats)
            genreAndFormat.text = getString(
                R.string.genre_and_format, genres[film.genre.ordinal], formats[film.format.ordinal]
            )
            comments.text = film.comments.ifBlank { getString(R.string.no_comments) }
            imdbButton.isEnabled = film.imdbUrl.isNotBlank()
        }
    }

    private fun openImdb() {
        val viewIntent = Intent(Intent.ACTION_VIEW, film.imdbUrl.toUri())
        try {
            startActivity(viewIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun editFilm() {
        val intent = Intent(this, FilmEditActivity::class.java)
        intent.putExtra(FilmEditActivity.EXTRA_FILM, film)
        editLauncher.launch(intent)
    }

    private fun goToMain() {
        val viewIntent = Intent(this, FilmListActivity::class.java)
        viewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(viewIntent)
    }

    private fun initUICompose() {
        setContent {
            FilmDataScreen(
                film = film,
                onImdb = { openImdb() },
                onEdit = { editFilm() },
                onMain = { goToMain() },
            )
        }
    }
}

@Composable
fun FilmDataScreen(
    film: Film,
    onImdb: () -> Unit,
    onEdit: () -> Unit,
    onMain: () -> Unit,
) {
    val landscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    MyWindow {
        // The screen does not fit on a small screen, or in landscape, so it scrolls
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (landscape) {
                // Poster on the left, data in the middle, buttons on the right (from 70% of the width)
                Row(Modifier.fillMaxWidth()) {
                    Row(Modifier.weight(0.7f).padding(start = 32.dp, end = 16.dp)) {
                        FilmPoster(film, Modifier.size(150.dp, 206.dp))
                        FilmInfo(film, Modifier.weight(1f).padding(start = 16.dp))
                    }
                    FilmButtons(film, onImdb, onEdit, onMain, Modifier.weight(0.3f))
                }
            } else {
                Row(Modifier.fillMaxWidth()) {
                    FilmPoster(film, Modifier.size(160.dp, 220.dp))
                    FilmInfo(film, Modifier.weight(1f).padding(start = 16.dp))
                }
                Spacer(Modifier.height(24.dp))
                FilmButtons(
                    film, onImdb, onEdit, onMain,
                    Modifier.widthIn(max = 400.dp).fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(16.dp))
            MyText("${stringResource(R.string.using_mode)} ${GlobalMode.name}")
        }
    }
}

@Composable
private fun FilmPoster(film: Film, modifier: Modifier) {
    PosterImage(
        image = film.image,
        imageResId = film.imageResId,
        description = stringResource(R.string.poster_description, film.title),
        modifier = modifier,
    )
}

@Preview(showSystemUi = true, name = "Portrait")
@Preview(
    showSystemUi = true, name = "Landscape",
    device = "spec:width=891dp,height=411dp,orientation=landscape",
)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilmDataScreenPreview() {
    FilmDataScreen(film = sampleFilms().first(), onImdb = {}, onEdit = {}, onMain = {})
}

@Composable
private fun FilmInfo(film: Film, modifier: Modifier) {
    val genres = stringArrayResource(R.array.genres)
    val formats = stringArrayResource(R.array.formats)
    val bodyStyle = MaterialTheme.typography.bodyLarge
    val labelStyle = MaterialTheme.typography.labelLarge
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier) {
        Text(film.title, style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.director_hint), style = labelStyle, color = labelColor)
        Text(film.director, style = bodyStyle)

        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.year_hint), style = labelStyle, color = labelColor)
        Text(film.year.toString(), style = bodyStyle)

        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(
                R.string.genre_and_format,
                genres[film.genre.ordinal],
                formats[film.format.ordinal],
            ),
            style = bodyStyle,
        )

        Spacer(Modifier.height(16.dp))
        Text(
            film.comments.ifBlank { stringResource(R.string.no_comments) },
            style = bodyStyle,
            fontStyle = FontStyle.Italic,
        )
    }
}

@Composable
private fun FilmButtons(
    film: Film,
    onImdb: () -> Unit,
    onEdit: () -> Unit,
    onMain: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val buttonModifier = Modifier.fillMaxWidth()
        OutlinedButton(onClick = onImdb, enabled = film.imdbUrl.isNotBlank(), modifier = buttonModifier) {
            Text(stringResource(R.string.view_imdb))
        }
        Button(onClick = onEdit, modifier = buttonModifier) {
            Text(stringResource(R.string.editFilm))
        }
        OutlinedButton(onClick = onMain, modifier = buttonModifier) {
            Text(stringResource(R.string.returnMain))
        }
    }
}
