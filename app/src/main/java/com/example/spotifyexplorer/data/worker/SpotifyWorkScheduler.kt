package com.example.spotifyexplorer.data.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Schedules a background worker to refresh the Spotify token.
 * this method is called from the SpotifyApplication which runs before any activity
 */

fun scheduleSpotifyTokenRefresh(context: Context, clientId: String, clientSecret: String) {
    Log.d("WorkInit", "Token refresh worker scheduled")
    val inputData = Data.Builder()
        .putString("CLIENT_ID", clientId)
        .putString("CLIENT_SECRET", clientSecret)
        .build()

    // Periodic work request to refresh token every 15 minutes
    val tokenWork = PeriodicWorkRequestBuilder<SpotifyTokenWorker>(
        15, TimeUnit.MINUTES
    ) // run every 15 minutes
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "spotify_token_refresh",
        ExistingPeriodicWorkPolicy.REPLACE,
        tokenWork
    )
}