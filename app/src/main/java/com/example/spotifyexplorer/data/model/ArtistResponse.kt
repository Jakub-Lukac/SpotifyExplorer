package com.example.spotifyexplorer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ArtistResponse(
    val artists: Artists
)

@Serializable
data class Artists(
    val items: List<Artist>
)

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val popularity: Int,
    val followers: Followers,
    val images: List<Image>
)

@Serializable
data class Followers(val total: Int)
@Serializable
data class Image(val url: String, val height: Int?, val width: Int?)