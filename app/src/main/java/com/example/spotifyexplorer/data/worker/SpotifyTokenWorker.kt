package com.example.spotifyexplorer.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.datastore.TokenDataStore

class SpotifyTokenWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val tokenStore = TokenDataStore(context)

    override suspend fun doWork(): Result {
        try {
            val clientId = inputData.getString("CLIENT_ID") ?: return Result.failure()
            val clientSecret = inputData.getString("CLIENT_SECRET") ?: return Result.failure()

            val spotifyService = SpotifyService(clientId, clientSecret, tokenStore)
            val token = spotifyService.getValidAccessToken()

            val expiration = System.currentTimeMillis() + 55 * 60 * 1000 // example 55 mins
            tokenStore.saveToken(token, expiration)

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}
