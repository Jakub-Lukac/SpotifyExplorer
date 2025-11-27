package com.example.spotifyexplorer.data.ui.favoriteTracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository

/**
 * Purpose of the factory is to create ViewModel with parameter(s)
 */

class FavoriteTracksViewModelFactory(
    private val repository: FavoriteTrackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteTracksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteTracksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

