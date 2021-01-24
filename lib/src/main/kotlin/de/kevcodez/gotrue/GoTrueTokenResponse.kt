package de.kevcodez.gotrue
data class GoTrueTokenResponse(
        val accessToken: String,
        val tokenType: String,
        val expiresIn: Long,
        val refreshToken: String
)
