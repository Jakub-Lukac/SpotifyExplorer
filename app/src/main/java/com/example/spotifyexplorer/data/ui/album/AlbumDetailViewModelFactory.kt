package com.example.spotifyexplorer.data.ui.album

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.db.AppDatabase
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository

class AlbumDetailViewModelFactory(
    private val context: Context,
    private val tokenStore: TokenDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            val repository = FavoriteTrackRepository(db.favoriteTrackDao())

            @Suppress("UNCHECKED_CAST")
            return AlbumDetailViewModel(tokenStore, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
