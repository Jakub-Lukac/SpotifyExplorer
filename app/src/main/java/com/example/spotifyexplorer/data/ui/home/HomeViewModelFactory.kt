package com.example.spotifyexplorer.data.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyexplorer.data.ui.home.HomeViewModel
import com.example.spotifyexplorer.data.datastore.TokenDataStore


class HomeViewModelFactory(
    private val tokenStore: TokenDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(tokenStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
