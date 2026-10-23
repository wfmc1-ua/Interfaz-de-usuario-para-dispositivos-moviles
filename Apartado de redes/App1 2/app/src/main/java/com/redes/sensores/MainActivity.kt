package com.redes.sensores

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme = isSystemInDarkTheme()
            MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
                MainSensores(
                    onSensor = { onAceleromoetro() },
                    onCamera = { onCamera() }
                )
            }
        }
    }
    private fun onAceleromoetro(){
        val viewIntent = Intent(this@MainActivity, AcelerometroActivity::class.java)
        viewIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        try {
            startActivity(viewIntent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this, "No disponible", Toast.LENGTH_LONG).show()
        }
    }

    private fun onCamera(){
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(this, "No disponible", Toast.LENGTH_LONG).show()
        }
    }


}

@Composable
fun MainSensores(onSensor: () -> Unit, onCamera: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = onSensor) { Text("Acelerómetro") }
            Button(onClick = onCamera) { Text("Cámara") }
        }
    }
}

