package io.supabase.gotrue.http

/**
 * Exception is used when a bad status code (> 301) is returned.
 *
 * If you implement your custom GoTrueHttpClient, you need to handle exceptions on your own.
 *
 * @property[status] HTTP status code
 * @property[data] Response body as [String] if available
 */
class GoTrueHttpException(val status: Int, val data: String?) : RuntimeException("Unexpected response status: $status")