package es.ua.eps.filmoteca

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"
    }
    private lateinit var bindings: ActivityFilmDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableEdgeToEdgeWithAppBar()
         var titlel= intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.filmData)
        initUI(titlel)
    }

    private fun initUI(titlel: String){
        when(GlobalMode){
            Mode.Compose -> initUICompose()
            Mode.Bindings -> initUIBindings(titlel)

        }
    }
    private fun initUIBindings(titlel: String) {
        var bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.filmData)
        setSupportActionBar(bindings.appBar.toolbar)
        bindings.DataText.text = titlel
        val button1 = bindings.viewFilmButton
        val button2 = bindings.editFilmButton
        val button3 = bindings.backtoMainButton
        val mode = bindings.mode

        button1.setOnClickListener { viewFilm() }
        button2.setOnClickListener { editFilm() }
        button3.setOnClickListener {  goToMain()}
        mode.text = "${getString(R.string.using_mode)}  Bindings"

    }

    private fun viewFilm(){
        val viewIntent = Intent(this@FilmDataActivity, FilmDataActivity::class.java)
        viewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun editFilm(){
        val viewIntent = Intent(this@FilmDataActivity, FilmEditActivity::class.java)
        viewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToMain(){
        val viewIntent = Intent(this@FilmDataActivity, FilmListActivity::class.java)
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
