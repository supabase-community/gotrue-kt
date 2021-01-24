package de.kevcodez.gotrue.types

data class GoTrueTokenResponse(
        val accessToken: String,
        val tokenType: String,
        val expiresIn: Long,
        val refreshToken: String
)
