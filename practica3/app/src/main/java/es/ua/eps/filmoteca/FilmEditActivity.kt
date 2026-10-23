package es.ua.eps.filmoteca

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding

class FilmEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM = "es.ua.eps.filmoteca.extra.FILM"
        private const val STATE_GENRE = "genre"
        private const val STATE_FORMAT = "format"
        private const val STATE_IMAGE = "image"
        private const val STATE_IMAGE_URL = "imageUrl"
    }

    private lateinit var bindings: ActivityFilmEditBinding
    private lateinit var film: Film

    private var genreIndex = 0
    private var formatIndex = 0

    // Image chosen with the camera or the gallery. State, so the Compose screen recomposes
    private var image by mutableStateOf<Bitmap?>(null)
    private var imageUrl: String? = null

    // The camera returns a small Bitmap, enough for the poster. null if closed without a photo
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            imageUrl = null
            showImage(bitmap)
        }
    }

    // The system photo picker returns the Uri of the image
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // uri is null if the user closed the picker without choosing
        if (uri != null) {
            val bitmap = try {
                loadScaledBitmap(uri)
            } catch (e: Exception) {
                null
            }
            if (bitmap != null) {
                imageUrl = uri.toString()
                showImage(bitmap)
            } else {
                Toast.makeText(this, R.string.image_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableEdgeToEdgeWithAppBar()

        film = IntentCompat.getParcelableExtra(intent, EXTRA_FILM, Film::class.java)
            ?: sampleFilms().first()
        genreIndex = savedInstanceState?.getInt(STATE_GENRE) ?: film.genre.ordinal
        formatIndex = savedInstanceState?.getInt(STATE_FORMAT) ?: film.format.ordinal
        if (savedInstanceState != null) {
            image = BundleCompat.getParcelable(savedInstanceState, STATE_IMAGE, Bitmap::class.java)
            imageUrl = savedInstanceState.getString(STATE_IMAGE_URL)
        } else {
            image = film.image
            imageUrl = film.imageUrl
        }

        initUI(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_GENRE, genreIndex)
        outState.putInt(STATE_FORMAT, formatIndex)
        outState.putParcelable(STATE_IMAGE, image)
        outState.putString(STATE_IMAGE_URL, imageUrl)
    }

    private fun initUI(savedInstanceState: Bundle?) {
        when (GlobalMode) {
            Mode.Compose -> initUICompose()
            Mode.Bindings -> initUIBindings(savedInstanceState)
        }
    }

    //-------------------------------------
    // Bindings
    private fun initUIBindings(savedInstanceState: Bundle?) {
        bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            poster.showPoster(image, film.imageResId)
            poster.contentDescription = getString(R.string.poster_description, film.title)

            // The text fields restore themselves after a rotation, so they are only filled the
            // first time. Otherwise the user's changes would be lost
            if (savedInstanceState == null) {
                title.setText(film.title)
                director.setText(film.director)
                year.setText(film.year.toString())
                imdbUrl.setText(film.imdbUrl)
                comments.setText(film.comments)
            }

            // The dropdowns do not save their state (saveEnabled="false"): they are set from the
            // position every time. With false, setText does not filter the list down to one item
            val genres = resources.getStringArray(R.array.genres)
            val formats = resources.getStringArray(R.array.formats)
            genre.setText(genres[genreIndex], false)
            format.setText(formats[formatIndex], false)
            genre.setOnItemClickListener { _, _, position, _ -> genreIndex = position }
            format.setOnItemClickListener { _, _, position, _ -> formatIndex = position }

            takePhotoButton.setOnClickListener { takePhoto() }
            selectImageButton.setOnClickListener { selectImage() }
            saveButton.setOnClickListener {
                save(
                    film.copy(
                        title = title.text.toString(),
                        director = director.text.toString(),
                        year = year.text.toString().toIntOrNull() ?: 0,
                        genre = Film.Genre.entries[genreIndex],
                        format = Film.Format.entries[formatIndex],
                        imdbUrl = imdbUrl.text.toString(),
                        comments = comments.text.toString(),
                        image = image,
                        imageUrl = imageUrl,
                    )
                )
            }
            cancelButton.setOnClickListener { cancel() }

            mode.text = getString(R.string.using_mode) + " " + GlobalMode.name
        }
    }

    //-------------------------------------
    // Optional exercise: the photo and the image replace the poster
    private fun takePhoto() {
        try {
            takePhotoLauncher.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun selectImage() {
        selectImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showImage(bitmap: Bitmap) {
        image = bitmap
        if (GlobalMode == Mode.Bindings) bindings.poster.setImageBitmap(bitmap)
    }

    private fun save(edited: Film) {
        val result = Intent()
        result.putExtra(EXTRA_FILM, edited)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun cancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //-------------------------------------
    // Compose
    private fun initUICompose() {
        setContent {
            FilmEditScreen(
                film = film,
                image = image,
                onTakePhoto = { takePhoto() },
                onSelectImage = { selectImage() },
                onSave = { save(it.copy(image = image, imageUrl = imageUrl)) },
                onCancel = { cancel() },
            )
        }
    }
}

@Composable
fun FilmEditScreen(
    film: Film,
    image: Bitmap?,
    onTakePhoto: () -> Unit,
    onSelectImage: () -> Unit,
    onSave: (Film) -> Unit,
    onCancel: () -> Unit,
) {
    // rememberSaveable keeps the changes after a rotation
    var title by rememberSaveable { mutableStateOf(film.title) }
    var director by rememberSaveable { mutableStateOf(film.director) }
    var year by rememberSaveable { mutableStateOf(film.year.toString()) }
    var genreIndex by rememberSaveable { mutableIntStateOf(film.genre.ordinal) }
    var formatIndex by rememberSaveable { mutableIntStateOf(film.format.ordinal) }
    var imdbUrl by rememberSaveable { mutableStateOf(film.imdbUrl) }
    var comments by rememberSaveable { mutableStateOf(film.comments) }

    val fieldModifier = Modifier.fillMaxWidth()

    MyWindow {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PosterImage(
                    image = image,
                    imageResId = film.imageResId,
                    description = stringResource(R.string.poster_description, film.title),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(64.dp, 88.dp),
                )
                OutlinedButton(onClick = onTakePhoto, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.take_photo))
                }
                OutlinedButton(onClick = onSelectImage, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.select_image))
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title_hint)) },
                singleLine = true,
                modifier = fieldModifier,
            )
            OutlinedTextField(
                value = director,
                onValueChange = { director = it },
                label = { Text(stringResource(R.string.director_hint)) },
                singleLine = true,
                modifier = fieldModifier,
            )
            OutlinedTextField(
                value = year,
                // Only numbers
                onValueChange = { new -> if (new.length <= 4 && new.all { it.isDigit() }) year = new },
                label = { Text(stringResource(R.string.year_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = fieldModifier,
            )
            Dropdown(
                label = stringResource(R.string.genre_hint),
                options = stringArrayResource(R.array.genres),
                selected = genreIndex,
                onSelected = { genreIndex = it },
            )
            Dropdown(
                label = stringResource(R.string.format_hint),
                options = stringArrayResource(R.array.formats),
                selected = formatIndex,
                onSelected = { formatIndex = it },
            )
            OutlinedTextField(
                value = imdbUrl,
                onValueChange = { imdbUrl = it },
                label = { Text(stringResource(R.string.imdb_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = fieldModifier,
            )
            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text(stringResource(R.string.comments)) },
                minLines = 3,
                modifier = fieldModifier,
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        onSave(
                            film.copy(
                                title = title,
                                director = director,
                                year = year.toIntOrNull() ?: 0,
                                genre = Film.Genre.entries[genreIndex],
                                format = Film.Format.entries[formatIndex],
                                imdbUrl = imdbUrl,
                                comments = comments,
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.save))
                }
            }

            Spacer(Modifier.height(8.dp))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                MyText("${stringResource(R.string.using_mode)} ${GlobalMode.name}")
            }
        }
    }
}

@Preview(showSystemUi = true, name = "Light Mode")
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilmEditScreenPreview() {
    FilmEditScreen(
        film = sampleFilms().first(),
        image = null,
        onTakePhoto = {},
        onSelectImage = {},
        onSave = {},
        onCancel = {},
    )
}

// The two dropdowns only differ in the options, so they share this composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    label: String,
    options: Array<String>,
    selected: Int,
    onSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = options[selected],
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(index)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
