package com.example.spotifyexplorer.data.api

import com.example.spotifyexplorer.data.model.AlbumResponse
import com.example.spotifyexplorer.data.model.ArtistResponse
import com.example.spotifyexplorer.data.model.TrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// Based on the "Get data from the internet" section in android course

/**
 * Spotify API interface for data retrieval (artists, albums, tracks)
 * It uses retrofit annotations to declare HTTP requests and their parameters
 */
interface SpotifyApi {
    /**
     * Search for an artist by name.
     * */
    @GET("search") // defines path /search + HTTP method -> GET
    // By calling this method from the SpotifyApi instance in SpotifyService.kt with baseUrl
    // Retrofit does the work behind the scenes and constructs the final url
    suspend fun searchArtist(
        @Header("Authorization") bearer: String, // Bearer token from the token endpoint
        @Query("q") artistName: String, // The artist name to search for
        @Query("type") type: String = "artist" // Optional: restricting search to artist results
    ): Response<ArtistResponse>
    // Final url: https://api.spotify.com/v1/search/?q=Adele&type=artist (GET)
    // with bearer token authorization

    /**
     * Get albums by a specific artist ID.
     * Example: GET https://api.spotify.com/v1/artists/{id}/albums
     */
    @GET("artists/{artistId}/albums")
    suspend fun getArtistAlbums(
        @Header("Authorization") bearer: String,
        @Path("artistId") artistId: String,
    ): Response<AlbumResponse>
    // Final url: https://api.spotify.com/v1/artists/246dkjvS1zLTtiykXe5h60/albums
    // with bearer token authorization

    /**
     * Get tracks by a specific album ID.
     * Example: GET https://api.spotify.com/v1/album/{id}/tracks
     */
    @GET("albums/{albumId}/tracks")
    suspend fun  getAlbumTracks(
        @Header("Authorization") bearer: String,
        @Path("albumId") albumId: String,
    ) : Response<TrackResponse>
    // Final url: https://api.spotify.com/v1/albums/246dkjvS1zLTtiykXe5h60/tracks
    // with bearer token authorization
}