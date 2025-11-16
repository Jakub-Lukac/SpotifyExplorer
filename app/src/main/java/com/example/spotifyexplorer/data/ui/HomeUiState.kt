package com.example.spotifyexplorer.data.ui

import com.example.spotifyexplorer.data.model.Artist
import com.example.spotifyexplorer.data.model.Album

/**
 * Represents the different states of the Spotify search UI.
 */
sealed class UiState {
    object Idle : UiState() // Nothing searched yet
    object Loading : UiState() // Currently fetching data
    data class Success(val artist: Artist, val albums: List<Album>) : UiState() // Data loaded successfully
    data class Error(val message: String) : UiState() // Error occurred
}
