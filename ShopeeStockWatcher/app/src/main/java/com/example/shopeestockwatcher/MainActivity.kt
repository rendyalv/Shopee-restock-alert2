package com.example.shopeewatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.*
import com.example.shopeewatcher.ui.theme.ShopeeWatcherTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopeeWatcherTheme {
                // TODO: Add UI with input for product URL & Start Watching button
                // Example: startWatching("https://shopee.co.id/someproduct")
            }
        }
    }

    private fun startWatching(productUrl: String) {
        val oneTimeRequest = OneTimeWorkRequestBuilder<StockCheckWorker>()
            .setInputData(workDataOf("product_url" to productUrl))
            .build()
        WorkManager.getInstance(this).enqueue(oneTimeRequest)

        val periodicRequest = PeriodicWorkRequestBuilder<StockCheckWorker>(
            15, TimeUnit.MINUTES
        ).setInputData(workDataOf("product_url" to productUrl))
         .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StockCheck",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }
}