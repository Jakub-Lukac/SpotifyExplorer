package com.example.spotifyexplorer.data.api

import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Field
import com.example.spotifyexplorer.data.model.TokenResponse

/**
 * This interface handles authentication with the Spotify Accounts service using
 * the Client Credentials Flow. It allows the app to obtain an access token
 * which can then be used to access Spotify Web API endpoints.
 */
interface SpotifyAuthService {

    @FormUrlEncoded // specifies that request body will use form-encoded parameters
    @POST("api/token") // defines path /api/token + HTTP method -> POST
    suspend fun getAccessToken(
        // Adds the authorization header with client credentials
        @Header("Authorization") authHeader: String,
        // Sends form parameters in the request body
        @Field("grant_type") grantType: String = "client_credentials"
    ): Response<TokenResponse>
    // Final url: https://accounts.spotify.com/api/token (POST)
}
