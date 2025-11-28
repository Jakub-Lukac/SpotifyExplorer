package com.example.spotifyexplorer.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.datastore.TokenDataStore

/**
 * Worker to refresh Spotify token
 * Accepts clientId and clientSecret as input data
 * Uses TokenDataStore to save the token
 */

class SpotifyTokenWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val tokenStore = TokenDataStore(context)

    // suspend function to run async tasks in background
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
