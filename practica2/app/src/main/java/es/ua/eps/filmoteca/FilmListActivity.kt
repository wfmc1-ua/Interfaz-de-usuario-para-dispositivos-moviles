package es.ua.eps.filmoteca

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.content.Intent
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

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
        var bindings = ActivityFilmListBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.filmList)
        setSupportActionBar(bindings.appBar.toolbar)
        val button1 = bindings.filmAButton
        val button2 = bindings.filmBButton
        val button3 = bindings.about
        val mode = bindings.mode
        button1.setOnClickListener { viewFilm("Film A") }
        button2.setOnClickListener { viewFilm("Film B") }
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
     }
}
