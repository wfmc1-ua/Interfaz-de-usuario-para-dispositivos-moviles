package es.ua.eps.filmoteca

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import es.ua.eps.filmoteca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindings : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()
        initUI()
    }

    private fun initUI() {
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    private fun initUIBindings() {
        bindings = ActivityMainBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str
        }
    }

    private fun initUICompose() {
        setContent {
            MainScreen()
        }
    }
}

//-------------------------------------
@Composable
fun MainScreen() {
    MyWindow {
        val str = "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
        MyText(str)
    }
}

//-------------------------------------
@Preview(showSystemUi = true, name = "Light Mode")
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun MainScreenPreview() {
    MainScreen()
}
