package com.example.spotifyexplorer.data.ui.favoriteTracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository
import com.example.spotifyexplorer.data.model.FavoriteTrack
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val repository: FavoriteTrackRepository
) : ViewModel() {

    val favorites = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun addFavorite(track: FavoriteTrack, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val added = repository.addTrack(track)
            onResult(added)
        }
    }

    fun removeFavorite(trackId: String) {
        viewModelScope.launch {
            repository.removeTrack(trackId)
        }
    }
}