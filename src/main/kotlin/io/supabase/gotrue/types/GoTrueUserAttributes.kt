package io.supabase.gotrue.types

data class GoTrueUserAttributes(
        val email: String? = null,
        val password: String? = null,
        val data: Map<String, Any>? = null
)