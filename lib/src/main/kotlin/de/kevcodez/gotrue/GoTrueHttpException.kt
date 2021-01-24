package de.kevcodez.gotrue

class GoTrueHttpException(val status: Int, val httpBody: String?) : RuntimeException("Unexpected response status: $status")