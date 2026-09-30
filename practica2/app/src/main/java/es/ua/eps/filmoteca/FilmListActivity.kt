package es.ua.eps.filmoteca

import android.os.Bundle
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
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.main)
        setSupportActionBar(bindings.appBar.toolbar)
        val button1 = bindings.filmAButton
        val button2 = bindings.filmBButton
        val button3 = bindings.about

    }

    private fun initUICompose(){ //joho
     }
}
