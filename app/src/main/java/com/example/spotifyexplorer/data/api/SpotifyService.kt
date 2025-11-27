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
    // Accepts client id and secret as strings
    // And also tokenStore to be able to store and retrieve tokens
    private val clientId: String,
    private val clientSecret: String,
    private val tokenStore: TokenDataStore
) {
    private val authBaseUrl = "https://accounts.spotify.com/" // Used only to request access tokens
    private val apiBaseUrl = "https://api.spotify.com/v1/" // Used for actual API data requests
    private var accessToken: String? = null // var - mutable, we can change the value
    private var tokenExpiration: Long = 0L // Used to check if token is still valid
    private val mutex = Mutex() // Prevents multiple requests refreshing token at once
    private val json = Json { ignoreUnknownKeys = true } // Allows parsing JSON even if extra fields exist

    /**
     * Retrofit service used ONLY for authentication (passing the authBaseUrl).
     */
    private val authApi: SpotifyAuthService by lazy {
        Retrofit.Builder()
            .baseUrl(authBaseUrl) // Only used for authentication
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())) // tells Retrofit how to convert JSON
            .build() // creates Retrofit instance
            .create(SpotifyAuthService::class.java) // creates SpotifyAuthService instance
    }

    /**
     * Retrofit API client for data requests — searching artists, getting albums/tracks.
     * Kept separate from auth because it needs the token injected in each call.
     */
    private val api: SpotifyApi by lazy {
        Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(OkHttpClient.Builder().build())
            .build()
            .create(SpotifyApi::class.java)
    }

    /**
     * Returns a valid Spotify API access token
     * Mutex - prevents race conditions (multiple requests refreshing token at once)
     *
     * Flow of function:
     * Check if the token stored in DataStore is still valid
     * If it is expired or missing, then request new one
     * Save new token and exp time in DataStore
     * Return token
     */

    // suspend function to make it asynchronous and not block the calling thread
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
            val response = authApi.getAccessToken(
                clientId = clientId,
                clientSecret = clientSecret
            )
            if (response.isSuccessful) {
                val tokenResponse = response.body()!!
                accessToken = tokenResponse.access_token
                tokenExpiration = now + tokenResponse.expires_in * 1000
                if (accessToken == null) {
                    throw Exception("Invalid access token")
                }
                tokenStore.saveToken(accessToken!!, tokenExpiration)
                Log.d("Access Token", "New token saved")
            } else {
                throw Exception("Failed to obtain token")
            }
        }

        return accessToken ?: throw Exception("Invalid access token")
    }

    // API METHODS

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
            // The type of response comes from both the interface return type and the converter factory.
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