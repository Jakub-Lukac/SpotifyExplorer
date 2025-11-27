package com.example.spotifyexplorer.data.home

import com.example.spotifyexplorer.data.model.Artist
import com.example.spotifyexplorer.data.model.Album

/**
 * Represents the different states of the Spotify search UI (Home screen).
 * Sealed means that only these specific states are possible.
 */
sealed class HomeUiState {
    object Idle : HomeUiState() // Nothing searched yet
    object Loading : HomeUiState() // Currently fetching data
    data class Success(val artist: Artist, val albums: List<Album>) : HomeUiState() // Data loaded successfully
    data class Error(val message: String) : HomeUiState() // Error occurred
}