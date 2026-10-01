package es.ua.eps.filmoteca

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        var bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.filmEdit)
        setSupportActionBar(bindings.appBar.toolbar)

        val button1 = bindings.saveButton
        val button2 = bindings.cancelButton
        val mode = bindings.mode

        button1.setOnClickListener { finish() }
        button2.setOnClickListener { finish() }
        mode.text = "${getString(R.string.using_mode)}  Bindings"

    }

    private fun initUICompose(){ //joho
    }
}
