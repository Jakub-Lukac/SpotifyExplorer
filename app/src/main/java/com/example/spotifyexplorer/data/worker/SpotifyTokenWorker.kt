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
        return try {
            val clientId = inputData.getString("CLIENT_ID") ?: return Result.failure()
            val clientSecret = inputData.getString("CLIENT_SECRET") ?: return Result.failure()

            val spotifyService = SpotifyService(clientId, clientSecret, tokenStore)
            val token =
                spotifyService.getValidAccessToken() // This saves the token with correct expiration

            Log.d("SpotifyTokenWorker", "Refreshed token: $token")

            return Result.success()
        } catch (e: Exception) {
            Log.e("SpotifyTokenWorker", "Failed to refresh token", e)
            Result.retry()
        }
    }
}
