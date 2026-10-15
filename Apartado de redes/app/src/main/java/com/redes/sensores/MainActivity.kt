package com.redes.sensores

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    val sm = getSystemService(SensorManager::class.java)
    val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainSensores(onSensor ={ onResume()})
        }
    }

    override fun onResume() {
        super.onResume()
        sm.registerListener(this,acc, SensorManager.SENSOR_DELAY_UI)
    }

    fun onSensorChanged(e: SensorEvent){
        val (x,y,z) = e.values
    }
}

@Composable
fun MainSensores(onSensor:() -> Unit) {
 Scaffold() { Column(modifier = Modifier
     .fillMaxSize()
     .padding(it),
     horizontalAlignment = Alignment.CenterHorizontally,
     verticalArrangement = Arrangement.Center
 ){
     Button(onSensor){ Text("Acelerometro")}
 }
 }
}

//@Preview
//@Composable
//fun MainSensorePreview(){
//    MainSensores(onSensor = )
//}
