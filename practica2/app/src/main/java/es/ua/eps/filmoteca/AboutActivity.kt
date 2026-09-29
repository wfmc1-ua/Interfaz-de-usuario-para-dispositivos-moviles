package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var bindings : ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        var bindings = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.root.applySystemBarsPadding(bindings.appBar.root, bindings.sobre)
        setSupportActionBar(bindings.appBar.toolbar)
        val button1 = bindings.websiteButton
        val button2 = bindings.supportButton
        val button3 = bindings.backButton
        val mode = bindings.mode
        button1.setOnClickListener { openWebsite() }
        button2.setOnClickListener {sendEmail() }
        button3.setOnClickListener { finish() }

        mode.text = "${getString(R.string.using_mode)}  Bindings"
    }

    private fun openWebsite(){
        val viewIntent = Intent(Intent.ACTION_VIEW, "https://www.google.com".toUri())
        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }

    private fun sendEmail(){
        val viewIntent = Intent(Intent.ACTION_SENDTO, "wfmc1@gcloud.ua.es".toUri())
        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, R.string.no_app_available, Toast.LENGTH_LONG).show()
        }
    }



    private fun initUICompose() {
        setContent {
            AboutScreen()
        }
    }

    @SuppressLint("LocalContextGetResourceValueCall")
    @Composable
    fun AboutScreen() {
        val context = LocalContext.current
        MyWindow {
            Column(
                Modifier.weight(1f),
                horizontalAlignment  = Alignment.CenterHorizontally,
                verticalArrangement= Arrangement.Center) {
            Text(stringResource(R.string.creator))
                Image(
                    painter = painterResource(id = R.drawable.b427247ebed5ab1acca4a08855c0fd13),
                    contentDescription = "Imagen del creador" // O usa null si es decorativa
                )
                Button(onClick ={ Toast.makeText(
                    context,
                    context.getString(R.string.notImplemented),
                    Toast.LENGTH_LONG).show()} ){
                    Text(stringResource(R.string.webSite))
                }
                Button(onClick ={ Toast.makeText(
                    context,
                    context.getString(R.string.notImplemented),
                    Toast.LENGTH_LONG).show()} ){
                    Text(stringResource(R.string.support))
                }
                Button(onClick ={ Toast.makeText(
                    context,
                    context.getString(R.string.notImplemented),
                    Toast.LENGTH_LONG).show()} ){
                    Text(stringResource(R.string.back))
                }


            }
            MyText("${stringResource(R.string.using_mode)} ${GlobalMode.name}")

        }
    }

    @Preview(showSystemUi = true, name = "Light Mode")
    @Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode")
    @Composable
    fun AboutScreenPreview() {
        AboutScreen()
    }
}
