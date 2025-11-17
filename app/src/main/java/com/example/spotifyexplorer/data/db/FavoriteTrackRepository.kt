package com.example.spotifyexplorer.data.db

import com.example.spotifyexplorer.data.model.FavoriteTrack

class FavoriteTrackRepository(private val dao: FavoriteTrackDao) {

    fun getFavorites() = dao.getAllFavoriteTracks()

    suspend fun addTrack(track: FavoriteTrack): Boolean {
        return dao.insertTrack(track) != -1L
        // true = added, false = already existed
    }

    suspend fun removeTrack(trackId: String) {
        dao.deleteById(trackId)
    }

    suspend fun isFavorite(trackId: String): Boolean {
        return dao.isFavorite(trackId)
    }
}