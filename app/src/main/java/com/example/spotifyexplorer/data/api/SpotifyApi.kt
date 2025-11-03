package com.example.spotifyexplorer.data.api

import com.example.spotifyexplorer.data.model.ArtistResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Spotify Web API interface for data retrieval (artists, albums, tracks, etc.)
 */
interface SpotifyApi {
    @GET("search")
    suspend fun searchArtist(
        @Header("Authorization") bearer: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "artist"
    ): Response<ArtistResponse>
}