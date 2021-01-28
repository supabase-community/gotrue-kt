package io.supabase.gotrue.http

class GoTrueHttpException(val status: Int, val httpBody: String?) : RuntimeException("Unexpected response status: $status")