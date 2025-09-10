package com.example.shopeestockwatcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var inputUrl: EditText
    private lateinit var inputInterval: EditText
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var status: TextView

    private val prefsName = "ShopeeStockWatcherPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputUrl = findViewById(R.id.inputUrl)
        inputInterval = findViewById(R.id.inputInterval)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        status = findViewById(R.id.status)

        NotificationHelper.createChannel(this)

        // Load saved settings
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        inputUrl.setText(prefs.getString("url", ""))
        inputInterval.setText(prefs.getLong("interval", 5).toString())

        btnStart.setOnClickListener {
            val url = inputUrl.text.toString().trim()
            val intervalMin = inputInterval.text.toString().toLongOrNull() ?: 5

            // Save settings
            prefs.edit().putString("url", url).putLong("interval", intervalMin).apply()

            val i = Intent(this, ForegroundCheckService::class.java)
            i.putExtra("url", url)
            i.putExtra("interval", intervalMin)
            startService(i)

            status.text = "Watching every $intervalMin min: $url"
        }

        btnStop.setOnClickListener {
            stopService(Intent(this, ForegroundCheckService::class.java))
            status.text = "Stopped"
        }
    }
}
