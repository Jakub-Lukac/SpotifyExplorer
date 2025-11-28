package com.example.spotifyexplorer.data.ui.album

import com.example.spotifyexplorer.data.model.Album
import com.example.spotifyexplorer.data.model.Track
import com.example.spotifyexplorer.data.model.TrackResponse

/**
 * Represents the different states of the Album detail UI.
 * Sealed means that only these specific states are possible.
 */
sealed class AlbumUiState {
    object Idle : AlbumUiState()
    object Loading : AlbumUiState() // Currently fetching data
    data class Success(val album: Album, val tracks: TrackResponse) : AlbumUiState() // Data loaded successfully
    data class Error(val message: String) : AlbumUiState() // Error occurred
}