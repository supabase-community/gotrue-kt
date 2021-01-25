package de.kevcodez.gotrue.http

interface GoTrueHttpClient {

    fun post(path: String, headers: Map<String, String> = emptyMap(), data: Any? = null): String?

    fun put(path: String, headers: Map<String, String> = emptyMap(), data: Any): String

    fun get(path: String, headers: Map<String, String> = emptyMap()): String
}