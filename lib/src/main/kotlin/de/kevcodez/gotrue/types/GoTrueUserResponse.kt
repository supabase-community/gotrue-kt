package de.kevcodez.gotrue.types

import java.time.OffsetDateTime

data class GoTrueUserResponse(
        val id: String,
        val email: String,
        val confirmationSentAt: OffsetDateTime,
        val createdAt: OffsetDateTime,
        val updatedAt: OffsetDateTime
)