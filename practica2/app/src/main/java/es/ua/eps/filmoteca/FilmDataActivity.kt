package es.ua.eps.filmoteca

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"

    }
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val dato = result.data
            val editado = dato?.getStringExtra(EXTRA_FILM_TITLE)
            bindings.DataText.text = "${bindings.DataText.text} ${getString(R.string.edited)}"

        }
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
            Mode.Compose -> initUICompose(titlel)
            Mode.Bindings -> initUIBindings(titlel)

        }
    }
    private fun initUIBindings(titlel: String) {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.filmData)
        setSupportActionBar(bindings.appBar.toolbar)
        bindings.DataText.text = titlel

        val button1 = bindings.viewFilmButton
        val button2 = bindings.editFilmButton
        val button3 = bindings.backtoMainButton
        val mode = bindings.mode
        button1.text = "Ver ${titlel}"
        button2.text = "Editar ${titlel}"
        button1.setOnClickListener { viewFilm() }
        button2.setOnClickListener {
            editLauncher.launch(Intent(this, FilmEditActivity::class.java))
        }


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


    private fun initUICompose(titlel: String){ //joho
        setContent {
            filmDataScreen(
                text= titlel,
                onFilm = {viewFilm()},
                onEdit = {editFilm()},
                onMain ={goToMain()})
        }

    }

}

@Composable
fun filmDataScreen( text: String,
                    onFilm: () -> Unit,
                    onEdit: () -> Unit,
                    onMain: () -> Unit){

    MyWindow() {
        Column(Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DefaultDP.dp, Alignment.CenterVertically)
        ){
            Text(text)
            Button(onClick = onFilm, modifier = Modifier.size(150.dp, 60.dp)){Text("Ver ${text}")}
            Button(onClick = onEdit, modifier = Modifier.size(150.dp, 60.dp)){Text("Editar ${text}")}
            Button(onClick = onMain, modifier = Modifier.size(150.dp, 60.dp)){Text(stringResource(R.string.returnMain))}
        }
    }

}
