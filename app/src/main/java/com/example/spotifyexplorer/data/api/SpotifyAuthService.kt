package com.example.spotifyexplorer.data.api

import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Field
import com.example.spotifyexplorer.data.model.TokenResponse

/**
 * Handles authentication with Spotify Accounts Service.
 * Uses Client Credentials Flow to obtain an access token.
 */
interface SpotifyAuthService {

    @FormUrlEncoded
    @POST("api/token")
    suspend fun getAccessToken(
        @Header("Authorization") authHeader: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Response<TokenResponse>
}
