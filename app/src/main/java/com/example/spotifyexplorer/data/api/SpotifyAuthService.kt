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
 *
 * I had to first register application in the Spotify developer portal to
 * access the client id and client secret
 */
interface SpotifyAuthService {

    /**
     * Get an access token using the Client Credentials Flow.
     */
    @FormUrlEncoded // specifies that request body will use form-encoded parameters
    // In postman this would be inside the params tab
    @POST("api/token") // defines path /api/token + HTTP method -> POST
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Response<TokenResponse>
    // Final url: https://accounts.spotify.com/api/token (POST)
}
