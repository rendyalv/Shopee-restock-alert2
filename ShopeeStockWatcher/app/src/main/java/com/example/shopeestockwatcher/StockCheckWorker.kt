package com.example.shopeewatcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import timber.log.Timber

class StockCheckWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val url = inputData.getString("product_url") ?: return Result.failure()
        createNotificationChannel()
        if (!isValidShopeeUrl(url)) {
            sendNotification("Invalid URL", "This is not a valid Shopee product link")
            Timber.e("Invalid Shopee URL: $url")
            return Result.failure()
        }
        val inStock = checkStock(url)
        Timber.d("Checked stock for: $url")
        if (inStock) {
            sendNotification("Back in Stock!", "The product is now available")
        }
        return Result.success()
    }

    private fun isValidShopeeUrl(url: String): Boolean {
        val pattern = "https?://(www\\.)?shopee\\.co\\.id/.+".toRegex()
        return url.matches(pattern)
    }

    private suspend fun checkStock(url: String): Boolean {
        delay(1000)
        return (0..1).random() == 1
    }

    private fun sendNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(applicationContext, "StockChannel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(applicationContext).notify((0..9999).random(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "StockChannel",
                "Stock Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notifies when product is back in stock or invalid" }
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
