package es.ua.eps.filmoteca

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"
        private const val STATE_EDITED = "edited"
    }

    private lateinit var bindings: ActivityFilmDataBinding
    private lateinit var film: Film

    // State, so the Compose screen recomposes when it changes
    private var edited by mutableStateOf(false)

    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            edited = true
            if (GlobalMode == Mode.Bindings) showTitle()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableEdgeToEdgeWithAppBar()

        film = filmWithTitle(intent.getStringExtra(EXTRA_FILM_TITLE))
        edited = savedInstanceState?.getBoolean(STATE_EDITED) ?: false
        initUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_EDITED, edited)
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

            if (film.imageResId != 0) poster.setImageResource(film.imageResId)
            poster.contentDescription = getString(R.string.poster_description, film.title)
            showTitle()
            director.text = getString(R.string.director_format, film.director)
            year.text = getString(R.string.year_format, film.year)

            val genres = resources.getStringArray(R.array.genres)
            val formats = resources.getStringArray(R.array.formats)
            genreAndFormat.text = getString(
                R.string.genre_and_format, genres[film.genre.ordinal], formats[film.format.ordinal]
            )
            comments.text = film.comments.ifBlank { getString(R.string.no_comments) }

            imdbButton.isEnabled = film.imdbUrl.isNotBlank()
            imdbButton.setOnClickListener { openImdb() }
            editFilmButton.setOnClickListener { editFilm() }
            backtoMainButton.setOnClickListener { goToMain() }

            mode.text = getString(R.string.using_mode) + " " + GlobalMode.name
        }
    }

    private fun showTitle() {
        bindings.title.text =
            if (edited) "${film.title} ${getString(R.string.edited)}" else film.title
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
        editLauncher.launch(Intent(this, FilmEditActivity::class.java))
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
                edited = edited,
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
    edited: Boolean,
    onImdb: () -> Unit,
    onEdit: () -> Unit,
    onMain: () -> Unit,
) {
    val genres = stringArrayResource(R.array.genres)
    val formats = stringArrayResource(R.array.formats)
    val bodyStyle = MaterialTheme.typography.bodyLarge

    MyWindow {
        // The poster, the data and the buttons do not fit on a small screen, or in landscape
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth()) {
                if (film.imageResId != 0) {
                    Image(
                        painter = painterResource(film.imageResId),
                        contentDescription = stringResource(R.string.poster_description, film.title),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(160.dp, 220.dp),
                    )
                }
                Column(
                    Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val title =
                        if (edited) "${film.title} ${stringResource(R.string.edited)}" else film.title
                    Text(
                        title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.director_format, film.director), style = bodyStyle)
                    Text(stringResource(R.string.year_format, film.year), style = bodyStyle)
                    Text(
                        stringResource(
                            R.string.genre_and_format,
                            genres[film.genre.ordinal],
                            formats[film.format.ordinal],
                        ),
                        style = bodyStyle,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(R.string.comments),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                film.comments.ifBlank { stringResource(R.string.no_comments) },
                style = bodyStyle,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            )

            Spacer(Modifier.height(24.dp))
            val buttonModifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
            Button(onClick = onImdb, enabled = film.imdbUrl.isNotBlank(), modifier = buttonModifier) {
                Text(stringResource(R.string.view_imdb))
            }
            Button(onClick = onEdit, modifier = buttonModifier) {
                Text(stringResource(R.string.editFilm))
            }
            Button(onClick = onMain, modifier = buttonModifier) {
                Text(stringResource(R.string.returnMain))
            }

            Spacer(Modifier.height(24.dp))
            MyText("${stringResource(R.string.using_mode)} ${GlobalMode.name}")
        }
    }
}
