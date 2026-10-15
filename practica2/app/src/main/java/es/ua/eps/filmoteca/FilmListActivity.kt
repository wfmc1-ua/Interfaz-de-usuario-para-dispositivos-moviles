package es.ua.eps.filmoteca

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
import androidx.compose.foundation.layout.Column
import  androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
class FilmListActivity : AppCompatActivity() {

    private lateinit var bindings: ActivityFilmListBinding
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
        bindings = ActivityFilmListBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.filmList)
        setSupportActionBar(bindings.appBar.toolbar)
        val button1 = bindings.filmAButton
        val button2 = bindings.filmBButton
        val button3 = bindings.about
        val mode = bindings.mode
        button1.setOnClickListener { viewFilm("Avengers EndGame") }
        button2.setOnClickListener { viewFilm("Harry popoter") }
        button3.setOnClickListener {  goToAbout()}

        mode.text = "${getString(R.string.using_mode)}  Bindings"

    }
    private fun viewFilm(pelicula: String){
        val viewIntent = Intent(this@FilmListActivity, FilmDataActivity::class.java)
        viewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        viewIntent.putExtra(FilmDataActivity.EXTRA_FILM_TITLE,pelicula)
        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToAbout(){
        val viewIntent = Intent(this@FilmListActivity, AboutActivity::class.java)
        viewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun initUICompose(){ //joho
        setContent {
            filmListScreen(
            onFilmA = {viewFilm("Avengers EndGame")},
            onFilmB = {viewFilm("Harry popoter")},
            onAbout ={goToAbout()})
        }
 }


    @Composable
    fun filmListScreen(onFilmA: () -> Unit,
                       onFilmB: () -> Unit,
                       onAbout: () -> Unit)
    {
        val localContext = LocalContext.current
        MyWindow(){
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DefaultDP.dp, Alignment.CenterVertically)
        ){
            Button(onClick = onFilmA, modifier = Modifier.size(150.dp, 60.dp)){Text(stringResource(R.string.filmA))}
            Button(onClick = onFilmB, modifier = Modifier.size(150.dp, 60.dp)){Text(stringResource(R.string.filmB))}
            Button(onClick = onAbout, modifier = Modifier.size(150.dp, 60.dp)){Text(stringResource(R.string.about))}


        }}
    }


}

