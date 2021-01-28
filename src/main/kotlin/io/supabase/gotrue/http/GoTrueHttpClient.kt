package io.supabase.gotrue.http

/**
 * Interface used by the GoTrueClient, allows replacing the default HTTP client.
 *
 * Overwrite it to replace the default Apache HTTP Client implementation.
 */
interface GoTrueHttpClient {

    /**
     * Executes a HTTP POST request.
     *
     * @param[path] The path that will be added to the base uri
     *
     * @param[headers] The custom headers that will be added to the default headers.
     * Custom headers replace default headers if duplicate
     *
     * @param[data] The data that will be JSON-encoded and submitted as POST body
     *
     * @return The response body as [String]
     */
    fun post(path: String, headers: Map<String, String> = emptyMap(), data: Any? = null): String?

    /**
     * Executes a HTTP PUT request.
     *
     * @param[path] The path that will be added to the base uri
     *
     * @param[headers] The custom headers that will be added to the default headers.
     * Custom headers replace default headers if duplicate
     *
     * @param[data] The data that will be JSON-encoded and submitted as POST body
     *
     * @return The response body as [String]
     */
    fun put(path: String, headers: Map<String, String> = emptyMap(), data: Any): String

    /**
     * Executes a HTTP GET request.
     *
     * @param[path] The path that will be added to the base uri
     *
     * @param[headers] The custom headers that will be added to the default headers.
     * Custom headers replace default headers if duplicate
     *
     * @return The response body as [String]
     */
    fun get(path: String, headers: Map<String, String> = emptyMap()): String
}