package com.example.spotifyexplorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Custom model that is similar to TrackResponse model
 * Also used as entity in the favorite_tracks table
 * Id is primary key
*/

@Entity(tableName = "favorite_tracks")
data class FavoriteTrack(
    @PrimaryKey val id: String,
    val name: String,
    val duration_ms: Int,
    val track_number: Int,
    val albumImage: String?,
)