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

    // FavoriteTrackRepository gets initiated inside the FavoriteTracksViewModelFactory

    // Get all the favorite tracks from the repository
    val favorites = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * ViewModelScope is used inside viewModels to launch coroutines
     */
    fun removeFavorite(trackId: String) {
        viewModelScope.launch {
            repository.removeTrack(trackId)
        }
    }

    fun updateFavorite(track: FavoriteTrack){
        viewModelScope.launch {
            repository.updateTrack(track)
        }
    }
}