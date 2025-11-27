package com.example.spotifyexplorer.data.model

import kotlinx.serialization.Serializable

/*
 * Matches Artist response for artist endpoint
 * The spotify artist endpoint returns all instances where the searches artist name occurs
 * So that is why we need the additional wrappers
 * Later in spotify service logic we only retrieve the first record from the response
 * as that is the artist we are interested in
 */

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
    val images: List<Image>,
    val genres: List<String>
)

@Serializable
data class Followers(val total: Int)
@Serializable
data class Image(val url: String?, val height: Int?, val width: Int?)