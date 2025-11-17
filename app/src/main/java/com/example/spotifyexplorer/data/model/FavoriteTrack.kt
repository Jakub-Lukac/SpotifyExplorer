package com.example.spotifyexplorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrack(
    @PrimaryKey val id: String,        // Spotify track ID
    val name: String,
    val artist: String,
    val albumArtUrl: String? = null,   // optional
    val addedAt: Long = System.currentTimeMillis()
)