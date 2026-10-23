package com.redes.sensores

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt

class AcelerometroActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sm: SensorManager
    private var acc: Sensor? = null

    private lateinit var tvX: TextView
    private lateinit var tvY: TextView
    private lateinit var tvZ: TextView
    private lateinit var tvShake: TextView

    private var lastShakeTime = 0L
    private val SHAKE_THRESHOLD = 12f
    private val SHAKE_COOLDOWN_MS = 100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acelerometro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvX = findViewById(R.id.tvX)
        tvY = findViewById(R.id.tvY)
        tvZ = findViewById(R.id.tvZ)
        tvShake = findViewById(R.id.tvShake)

        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        acc?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sm.unregisterListener(this)
    }

    override fun onSensorChanged(e: SensorEvent) {
        val x = e.values[0]
        val y = e.values[1]
        val z = e.values[2]

        tvX.text = "X: %.2f m/s²".format(x)
        tvY.text = "Y: %.2f m/s²".format(y)
        tvZ.text = "Z: %.2f m/s²".format(z)

        val magnitude = sqrt(x * x + y * y + z * z)
        val net = magnitude - SensorManager.GRAVITY_EARTH

        val now = System.currentTimeMillis()
        if (net > SHAKE_THRESHOLD && now - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = now
            tvShake.visibility = android.view.View.VISIBLE
            tvShake.postDelayed({ tvShake.visibility = android.view.View.INVISIBLE }, 1500)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}
