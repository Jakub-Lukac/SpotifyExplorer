package com.example.spotifyexplorer.data.ui.album

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.db.AppDatabase
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository

class AlbumDetailViewModelFactory(
    private val tokenStore: TokenDataStore,
    private val repository: FavoriteTrackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumDetailViewModel(tokenStore, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
