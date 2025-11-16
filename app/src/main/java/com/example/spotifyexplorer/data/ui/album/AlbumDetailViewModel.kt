package com.example.spotifyexplorer.data.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.home.HomeUiState
import com.example.spotifyexplorer.data.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val tokenStore: TokenDataStore
) : ViewModel() {

    private val clientId = "03619e7aa0344a45a9fe014a969a62ea"
    private val clientSecret = "6c994bd714a94ee79d8f6c985716c3a9"

    private val spotifyService = SpotifyService(clientId, clientSecret, tokenStore)
    private val _uiState = MutableStateFlow<AlbumUiState>(AlbumUiState.Idle)
    val uiState: StateFlow<AlbumUiState> = _uiState

    fun loadAlbumTracks(album: Album) {
        viewModelScope.launch {
            _uiState.value = AlbumUiState.Loading
            try {
                val tracks = spotifyService.getAlbumTracks(album.id)
                if (tracks != null) {
                    _uiState.value = AlbumUiState.Success(album, tracks)
                } else {
                    _uiState.value = AlbumUiState.Error("Tracks not found")
                }
            } catch (e: Exception) {
                _uiState.value = AlbumUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}