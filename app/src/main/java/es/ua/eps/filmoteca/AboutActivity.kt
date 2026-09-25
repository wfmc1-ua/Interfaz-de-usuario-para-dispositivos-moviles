package es.ua.eps.filmoteca

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding
import es.ua.eps.filmoteca.databinding.ActivityMainBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var bindings : ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()
        var bindings = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.sobre)
        setSupportActionBar(bindings.appBar.toolbar)
    }
}
