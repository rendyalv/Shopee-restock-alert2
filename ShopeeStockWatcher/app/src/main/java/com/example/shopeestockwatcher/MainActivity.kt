package com.example.shopeewatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.*
import com.example.shopeewatcher.ui.theme.ShopeeWatcherTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopeeWatcherTheme {
                WatcherScreen { url ->
                    startWatching(url)
                }
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

@Composable
fun WatcherScreen(onStart: (String) -> Unit) {
    var url by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Shopee Product URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onStart(url) }, modifier = Modifier.fillMaxWidth()) {
            Text("Start Watching")
        }
    }
}
