package com.example.shopeestockwatcher

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.regex.Pattern

class ForegroundCheckService : Service() {
    private val client = OkHttpClient()
    private val handler = Handler()
    private var intervalMs: Long = 300000 // default 5 min
    private var url: String = ""

    private val task = object : Runnable {
        override fun run() {
            if (url.isNotEmpty()) {
                checkStock(url)
                handler.postDelayed(this, intervalMs)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        url = intent?.getStringExtra("url") ?: ""
        intervalMs = (intent?.getLongExtra("interval", 5L) ?: 5L) * 60 * 1000 // min to ms
        if (intervalMs < 60 * 1000) intervalMs = 60 * 1000 // minimum 1 minute sanity

        createChannel()
        val notif = buildNotification("Watching Shopee product…") 
        startForeground(1, notif)

        handler.post(task)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(task)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("fg_service", "Shopee Watch Service", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(content: String): Notification {
        return NotificationCompat.Builder(this, "fg_service")
            .setContentTitle("Shopee Stock Watcher")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()
    }

    private fun checkStock(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val html = fetchUrl(url)
                val inStock = parseStockFromHtml(html)
                if (inStock) {
                    NotificationHelper.sendNotification(applicationContext, "Back in stock!", "Product is available: $url")
                }
            } catch (e: Exception) {
                Log.e("ForegroundCheckService", "Check failed", e)
            }
        }
    }

    private fun fetchUrl(url: String): String {
        val req = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Android) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0 Mobile Safari/537.36")
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw Exception("HTTP ${resp.code}")
            return resp.body?.string() ?: ""
        }
    }

    private fun parseStockFromHtml(html: String): Boolean {
        val p = Pattern.compile("\"stock\"\s*:\s*(\\d+)")
        val m = p.matcher(html)
        if (m.find()) {
            val stock = m.group(1)?.toIntOrNull() ?: 0
            return stock > 0
        }
        if (html.contains("out of stock", true) || html.contains("kosong", true)) return false
        return false
    }
}
