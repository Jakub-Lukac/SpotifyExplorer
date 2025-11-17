package com.example.spotifyexplorer.data.api

import com.example.spotifyexplorer.data.model.Artist
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import android.util.Base64
import android.util.Log
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.model.AlbumResponse
import com.example.spotifyexplorer.data.model.TrackResponse
import kotlinx.coroutines.flow.firstOrNull

class SpotifyService(
    private val clientId: String,
    private val clientSecret: String,
    private val tokenStore: TokenDataStore
) {
    private val authBaseUrl = "https://accounts.spotify.com/"
    private val apiBaseUrl = "https://api.spotify.com/v1/"

    private var accessToken: String? = null
    private var tokenExpiration: Long = 0L
    private val mutex = Mutex()

    private val json = Json { ignoreUnknownKeys = true }

    private val authApi: SpotifyAuthService by lazy {
        Retrofit.Builder()
            .baseUrl(authBaseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SpotifyAuthService::class.java)
    }

    private val api: SpotifyApi by lazy {
        Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(OkHttpClient.Builder().build())
            .build()
            .create(SpotifyApi::class.java)
    }

    // Ensure valid token
    suspend fun getValidAccessToken(): String = mutex.withLock {
        val now = System.currentTimeMillis()

        val storedToken = tokenStore.accessToken.firstOrNull()
        val storedExpiration = tokenStore.expirationTime.firstOrNull() ?: 0L
        Log.d("Expiration", "$now")
        Log.d("Expiration", "$storedExpiration")

        if (storedToken != null && now < storedExpiration) {
            // Only use it if still valid
            accessToken = storedToken
            tokenExpiration = storedExpiration
            Log.d("Access Token", "Using stored token")
        } else {
            // Fetch a new token
            Log.d("Access Token", "Fetching new token")
            val credentials = "$clientId:$clientSecret"
            val basicAuth = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            val response = authApi.getAccessToken("Basic $basicAuth")
            if (response.isSuccessful) {
                val tokenResponse = response.body()!!
                accessToken = tokenResponse.access_token
                tokenExpiration = now + tokenResponse.expires_in * 1000
                tokenStore.saveToken(accessToken!!, tokenExpiration)
                Log.d("Access Token", "New token saved")
            } else {
                throw Exception("Failed to obtain token")
            }
        }

        return accessToken!!
    }

    // --- API methods ---

    suspend fun searchArtist(artistName: String): Artist? {
        Log.d("Search artist", "Searching for artist")
        val token = getValidAccessToken()
        val response = api.searchArtist("Bearer $token", artistName)

        if (response.isSuccessful) {
            return response.body()?.artists?.items?.firstOrNull()
        } else {
            throw Exception("Search artist failed: ${response.errorBody()?.string()}")
        }
    }

    suspend fun getArtistAlbums(artistId: String): AlbumResponse? {
        val token = getValidAccessToken()
        val response = api.getArtistAlbums("Bearer $token", artistId)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Failed to fetch albums: ${response.errorBody()?.string()}")
        }
    }

    suspend fun getAlbumTracks(albumId: String): TrackResponse? {
        val token = getValidAccessToken()
        val response = api.getAlbumTracks(bearer = "Bearer $token", albumId)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Failed to fetch tracks: ${response.errorBody()?.string()}")
        }
    }

}