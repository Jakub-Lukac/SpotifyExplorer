package com.example.spotifyexplorer.data.model

import kotlinx.serialization.Serializable

/*
 * Matches response for token endpoint
 */
@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
