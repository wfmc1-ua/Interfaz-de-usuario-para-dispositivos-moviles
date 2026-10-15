package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding

class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableEdgeToEdgeWithAppBar()
        initUI()
    }

    private fun initUI(){
        when(GlobalMode){
            Mode.Compose -> initUICompose()
            Mode.Bindings -> initUIBindings()

        }
    }
    private fun initUIBindings() {
        bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.filmEdit)
        setSupportActionBar(bindings.appBar.toolbar)

        val button1 = bindings.saveButton
        val button2 = bindings.cancelButton
        val mode = bindings.mode
        button1.setOnClickListener {
            setResult(Activity.RESULT_OK,Intent().putExtra(FilmDataActivity.EXTRA_FILM_TITLE, "Nuevo título"))
            finish() }
        button2.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() }
        mode.text = "${getString(R.string.using_mode)}  Bindings"

    }

    private fun save(){
        setResult(Activity.RESULT_OK,Intent().putExtra(FilmDataActivity.EXTRA_FILM_TITLE, "Nuevo título"))
        finish()
    }

    private fun cancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun initUICompose(){
        setContent {
            filmEditScreen(onSave = {save()}, onCancel =  {cancel()})

        }
    }
}

@Composable
fun filmEditScreen(onSave: () -> Unit, onCancel: () -> Unit){

    MyWindow() {
        Column(Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DefaultDP.dp, Alignment.CenterVertically)
        ){
            Text(stringResource(R.string.editingFilm))
            Button(onClick = onSave, modifier = Modifier.size(150.dp, 60.dp)){Text(stringResource(R.string.save))}
            Button(onClick = onCancel, modifier = Modifier.size(150.dp, 60.dp)){Text(stringResource(R.string.cancel))}
            }
        }
}


