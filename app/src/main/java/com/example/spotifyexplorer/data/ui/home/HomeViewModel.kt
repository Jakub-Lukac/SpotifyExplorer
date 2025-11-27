package com.example.spotifyexplorer.data.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.model.Album
import com.example.spotifyexplorer.data.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tokenStore: TokenDataStore
) : ViewModel() {
    private val clientId = "03619e7aa0344a45a9fe014a969a62ea"
    private val clientSecret = "6c994bd714a94ee79d8f6c985716c3a9"
    private val spotifyService = SpotifyService(clientId, clientSecret, tokenStore)

    // private mutable state flow that stored the current state of the Home screen, with initial state of Idle
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)

    // exposes state as read-only (val)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val selectedAlbum = _selectedAlbum.asStateFlow()

    fun selectAlbum(album: Album) {
        _selectedAlbum.value = album
    }

    fun searchArtist(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val artist = spotifyService.searchArtist(query)
                if (artist != null) {
                    val albumResponse = spotifyService.getArtistAlbums(artist.id)
                    val albums = albumResponse?.items ?: emptyList()
                    _uiState.value = HomeUiState.Success(artist, albums)
                } else {
                    _uiState.value = HomeUiState.Error("Artist not found")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
