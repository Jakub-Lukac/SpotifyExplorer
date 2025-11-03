package com.example.spotifyexplorer.data.repository

import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.model.Artist

/**
 * Repository responsible for fetching and caching artist data.
 * Keeps ViewModel logic clean and testable.
 */
class ArtistRepository {

    private val api = SpotifyService.api

    suspend fun searchArtists(query: String): List<Artist> {
        val response = api.searchArtists(query = query)
        return response.artists.items
    }
}