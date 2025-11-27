package com.example.spotifyexplorer.data.model

import kotlinx.serialization.Serializable

/*
 * Matches Album response for album endpoint of Spotify API
 */
@Serializable
data class AlbumResponse(
    val total: Int,
    val items: List<Album>
)

@Serializable
data class Album(
    val id: String,
    val name: String,
    val total_tracks: Int,
    val release_date: String,
    val images: List<Image>
)
