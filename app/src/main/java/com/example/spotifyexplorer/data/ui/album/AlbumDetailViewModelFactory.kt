package com.example.spotifyexplorer.data.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyexplorer.data.datastore.TokenDataStore

class AlbumDetailViewModelFactory(
    private val tokenStore: TokenDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumDetailViewModel(tokenStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
