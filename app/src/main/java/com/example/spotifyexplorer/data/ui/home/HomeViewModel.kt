package com.example.spotifyexplorer.data.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val clientId = "03619e7aa0344a45a9fe014a969a62ea"
    private val clientSecret = "6c994bd714a94ee79d8f6c985716c3a9"
    private val spotifyService = SpotifyService(clientId, clientSecret)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun searchArtist(query: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val artist = spotifyService.searchArtist(query)
                if (artist != null) {
                    val albumResponse = spotifyService.getArtistAlbums(artist.id)
                    val albums = albumResponse?.items ?: emptyList()
                    _uiState.value = UiState.Success(artist, albums)
                } else {
                    _uiState.value = UiState.Error("Artist not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
