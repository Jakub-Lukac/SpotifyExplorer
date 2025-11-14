package com.example.spotifyexplorer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    val items: List<Track>
)

@Serializable
data class Track(
    val id: String,
    val duration_ms: Int,
    val name: String,
    val track_number: Int
)