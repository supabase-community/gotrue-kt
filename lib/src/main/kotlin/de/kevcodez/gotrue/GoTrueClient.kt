package de.kevcodez.gotrue

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.hc.client5.http.ClientProtocolException
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.classic.methods.HttpPut
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity

class GoTrueClient(
        private val baseUrl: String,
        private val defaultHeaders: Map<String, String>
) {

    private val objectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    /**
     * Returns the publicly available settings for this gotrue instance.
     */
    fun settings(): GoTrueSettings {
        return get("/settings")
    }

    /**
     * Register a new user with an email and password.
     */
    fun signup(email: String, password: String): GoTrueUserResponse {
        return post("/signup", mapOf("email" to email, "password" to password))
    }

    /**
     * Invites a new user with an email.
     */
    fun invite(email: String): GoTrueUserResponse {
        return post("/invite", mapOf("email" to email))
    }

    /**
     * Verify a registration or a password recovery.
     * Type can be signup or recovery and the token is a token returned from either /signup or /recover.
     */
    fun verify(type: GoTrueVerifyType, token: String, password: String? = null): GoTrueTokenResponse {
        return post("/verify", mapOf("type" to type.name.toLowerCase(), "token" to token, "password" to password))
    }

    /**
     * Password recovery. Will deliver a password recovery mail to the user based on email address.
     */
    fun recover(email: String) {
        return post("/recover", mapOf("email" to email))
    }

    /**
     * Update a user (Requires authentication).
     * Apart from changing email/password, this method can be used to set custom user data.
     */
    fun updateUser(accessToken: String, email: String? = null, password: String? = null, data: Map<String, Any>? = null): GoTrueUserResponse {
        return put(
                path = "/user",
                data = mapOf("email" to email, "password" to password, "data" to data),
                customHeaders = mapOf("Authorization" to "Bearer $accessToken")
        )
    }

    /**
     * Get the JSON object for the logged in user
     */
    fun getUser(accessToken: String): GoTrueUserResponse {
        return get("/user", customHeaders = mapOf("Authorization" to "Bearer $accessToken"))
    }

    /**
     * This is an OAuth2 endpoint that currently implements the password, refresh_token, and authorization_code grant types
     */
    fun token(grantType: String, email: String? = null, password: String? = null, refreshToken: String? = null): GoTrueTokenResponse {
        return post("/token?grant_type=${grantType}", mapOf("email" to email, "password" to password))
    }

    /**
     * Logout a user.
     * This will revoke all refresh tokens for the user.
     * Remember that the JWT tokens will still be valid for stateless auth until they expires.
     */
    fun logout(accessToken: String) {
        return post(
                path = "/logout",
                customHeaders = mapOf("Authorization" to "Bearer $accessToken")
        )
    }

    private inline fun <reified T> post(path: String, data: Any? = null, customHeaders: Map<String, String> = emptyMap()): T {
        val httpClient = HttpClients.createDefault()

        return httpClient.use {
            val httpPost = HttpPost(baseUrl + path)
            if (data != null) {
                val dataAsString = objectMapper.writeValueAsString(data)
                httpPost.entity = StringEntity(dataAsString)
            }
            defaultHeaders.filter { !customHeaders.containsKey(it.key) }.forEach { (name, value) -> httpPost.addHeader(name, value) }
            customHeaders.forEach { (name, value) -> httpPost.addHeader(name, value) }

            return@use it.execute(httpPost, responseHandler<T>())
        }
    }

    private inline fun <reified T> put(path: String, data: Any? = null, customHeaders: Map<String, String> = emptyMap()): T {
        val httpClient = HttpClients.createDefault()

        return httpClient.use {
            val httpPut = HttpPut(baseUrl + path)
            if (data != null) {
                val dataAsString = objectMapper.writeValueAsString(data)
                httpPut.entity = StringEntity(dataAsString)
            }
            defaultHeaders.filter { !customHeaders.containsKey(it.key) }.forEach { (name, value) -> httpPut.addHeader(name, value) }
            customHeaders.forEach { (name, value) -> httpPut.addHeader(name, value) }

            return@use it.execute(httpPut, responseHandler<T>())
        }
    }


    private inline fun <reified T> get(path: String, customHeaders: Map<String, String> = emptyMap()): T {
        val httpClient = HttpClients.createDefault()

        return httpClient.use { httpClient ->
            val httpGet = HttpGet(baseUrl + path)
            defaultHeaders.filter { !customHeaders.containsKey(it.key) }.forEach { (name, value) -> httpGet.addHeader(name, value) }
            customHeaders.forEach { (name, value) -> httpGet.addHeader(name, value) }
            return@use httpClient.execute(httpGet, responseHandler<T>())
        }
    }

    private inline fun <reified T> responseHandler(): HttpClientResponseHandler<T> {
        return object : HttpClientResponseHandler<T> {
            override fun handleResponse(response: ClassicHttpResponse): T? {
                val status = response.code
                val entity = response.entity

                val entityAsString = if (entity != null) {
                    EntityUtils.toString(entity)
                } else {
                    null
                }

                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                    return if (entityAsString == null) null else objectMapper.readValue<T>(entityAsString)
                } else {
                    throw  ClientProtocolException("Unexpected response status: $status\n\n$entityAsString");
                }
            }
        }
    }

}
