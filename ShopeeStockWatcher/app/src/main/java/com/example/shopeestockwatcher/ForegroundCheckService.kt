package com.example.shopeestockwatcher

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ForegroundCheckService : Service() {

    private val client = OkHttpClient()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkShopeeStock()
        return START_STICKY
    }

    private fun checkShopeeStock() {
        val url = "https://shopee.com/api/v1/stock" // <-- replace with real endpoint

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ShopeeStock", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("ShopeeStock", "Unexpected code $it")
                        return
                    }

                    val body = it.body?.string() ?: ""
                    // ✅ Regex fix (double escape)
                    val cleaned = body.replace("\\s+".toRegex(), " ")
                    Log.d("ShopeeStock", "Response: $cleaned")
                }
            }
        })
    }
}
