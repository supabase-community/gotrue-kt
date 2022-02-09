package io.supabase.gotrue.types

import java.util.*

data class CustomGoTrueUserResponse(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val user: User
)

data class User(
    val id: UUID,
    val email: String,
    val phone: String

)
