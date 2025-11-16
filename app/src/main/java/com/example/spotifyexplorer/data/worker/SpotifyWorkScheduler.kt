package com.example.spotifyexplorer.data.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

fun scheduleSpotifyTokenRefresh(context: Context, clientId: String, clientSecret: String) {
    Log.d("WorkInit", "Token refresh worker scheduled")
    val inputData = Data.Builder()
        .putString("CLIENT_ID", clientId)
        .putString("CLIENT_SECRET", clientSecret)
        .build()

    val tokenWork = PeriodicWorkRequestBuilder<SpotifyTokenWorker>(
        15, TimeUnit.MINUTES
    )
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "spotify_token_refresh",
        ExistingPeriodicWorkPolicy.REPLACE,
        tokenWork
    )
}