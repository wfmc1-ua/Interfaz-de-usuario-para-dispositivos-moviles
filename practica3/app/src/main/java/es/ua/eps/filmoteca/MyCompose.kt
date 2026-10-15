package es.ua.eps.filmoteca

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.core.graphics.drawable.toBitmap

//-------------------------------------
const val DefaultDP : Int = 8
//-------------------------------------

//-------------------------------------
@Composable
fun defaultTextStyle(
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    fontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    letterSpacing: TextUnit = MaterialTheme.typography.titleMedium.letterSpacing,
): TextStyle {
    return MaterialTheme.typography.titleMedium.copy(
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontSize = fontSize,
        letterSpacing = letterSpacing,
    )
}

//-------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWindow(
    title: String = stringResource(R.string.app_name),
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    // Dynamic color would replace the app colors with the wallpaper ones on Android 12+
    AppTheme(dynamicColor = false) {
        Scaffold(
            topBar = {
                // Material 3 paints the app bar with surface, like the background. We use the
                // classic primary color from earlier Material versions to set it apart
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(
                            onClick = { onNavigationClick?.invoke() },
                            enabled = onNavigationClick != null
                        ) {
                            AppBarIcon()
                        }
                    },
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    }
}

//-------------------------------------
@Composable
fun AppBarIcon() {
    val context = LocalContext.current
    // painterResource cannot load layer-list or adaptive icon drawables
    val icon = remember(context) {
        AppCompatResources.getDrawable(context, R.drawable.app_bar_icon)!!
            .toBitmap()
            .asImageBitmap()
    }
    Image(bitmap = icon, contentDescription = null)
}

//-------------------------------------
@Composable
fun MyText(title : String) {
    Text(
        text = title,
        style = defaultTextStyle()
    )
}
