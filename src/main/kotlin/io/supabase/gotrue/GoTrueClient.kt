package io.supabase.gotrue

import io.supabase.gotrue.http.GoTrueHttpClient
import io.supabase.gotrue.http.GoTrueHttpClientApache
import io.supabase.gotrue.json.GoTrueJsonConverter
import io.supabase.gotrue.json.GoTrueJsonConverterJackson
import io.supabase.gotrue.types.*
import org.apache.hc.client5.http.impl.classic.HttpClients
import java.util.Locale

open class GoTrueClient<UserResponseClass : Any, TokenResponseClass : Any>(
    val goTrueHttpClient: GoTrueHttpClient,
    val goTrueJsonConverter: GoTrueJsonConverter,
    private val goTrueUserResponseClass: Class<UserResponseClass>,
    private val goTrueTokenResponseClass: Class<TokenResponseClass>
) {
    companion object {

        inline fun <reified UserResponseClass : Any, reified TokenResponseClass : Any> goTrueClient(
            goTrueHttpClient: GoTrueHttpClient,
            goTrueJsonConverter: GoTrueJsonConverter
        ): GoTrueClient<UserResponseClass, TokenResponseClass> =
            GoTrueClient(goTrueHttpClient, goTrueJsonConverter, UserResponseClass::class.java, TokenResponseClass::class.java)

        inline fun <reified UserResponseClass : Any, reified TokenResponseClass : Any> customApacheJacksonGoTrueClient(
            url: String,
            headers: Map<String, Any>
        ): GoTrueClient<UserResponseClass, TokenResponseClass> =
            GoTrueClient(
                goTrueHttpClient = GoTrueHttpClientApache(
                    url = url,
                    headers = headers,
                    httpClient = { HttpClients.createDefault() },
                    goTrueJsonConverter = GoTrueJsonConverterJackson()
                ),
                goTrueJsonConverter = GoTrueJsonConverterJackson(),
                UserResponseClass::class.java,
                TokenResponseClass::class.java
            )
        fun defaultGoTrueClient(
            url: String,
            headers: Map<String, Any>
        ): GoTrueClient<GoTrueUserResponse, GoTrueTokenResponse> =
            GoTrueClient(
                goTrueHttpClient = GoTrueHttpClientApache(
                    url = url,
                    headers = headers,
                    httpClient = { HttpClients.createDefault() },
                    goTrueJsonConverter = GoTrueJsonConverterJackson()
                ),
                goTrueJsonConverter = GoTrueJsonConverterJackson(),
                GoTrueUserResponse::class.java,
                GoTrueTokenResponse::class.java
            )
    }

    /**
     * @return the publicly available settings for this GoTrue instance.
     */
    fun settings(): GoTrueSettings {
        val response = goTrueHttpClient.get(
            url = "/settings"
        )

        return goTrueJsonConverter.deserialize(response, GoTrueSettings::class.java)
    }

    /**
     * Creates a new user using their [email] address.
     *
     * @param[email] The email address of the user.
     * @param[password] The password of the user.
     */
    fun signUpWithEmail(email: String, password: String): UserResponseClass {
        val response = goTrueHttpClient.post(
            url = "/signup",
            data = mapOf("email" to email, "password" to password)
        )!!

        return goTrueJsonConverter.deserialize(response, goTrueUserResponseClass)
    }

    /**
     * Sends an invite link to an [email] address.
     *
     * @param[email] The email address of the user.
     */
    fun inviteUserByEmail(email: String): UserResponseClass {
        val response = goTrueHttpClient.post(
            url = "/invite",
            data = mapOf("email" to email)
        )!!

        return goTrueJsonConverter.deserialize(response, goTrueUserResponseClass)
    }

    /**
     * Verify a registration or a password recovery.
     * Type can be signup or recovery and the token is a token returned from either /signup or /recover.
     */
    fun verify(type: GoTrueVerifyType, token: String, password: String? = null): TokenResponseClass {
        val response = goTrueHttpClient.post(
            url = "/verify",
            data = mapOf("type" to type.name.lowercase(Locale.getDefault()), "token" to token, "password" to password),
        )!!

        return goTrueJsonConverter.deserialize(response, goTrueTokenResponseClass)
    }

    /**
     * Sends a reset request to an [email] address.
     *
     * ### Notes
     *
     * Sends a reset request to an email address.
     *
     * When the user clicks the reset link in the email they will be forwarded to:
     * *<SITE_URL>#access_token=x&refresh_token=y&expires_in=z&token_type=bearer&type=recovery*
     *
     * Your app must detect type=recovery in the fragment and display a password reset form to the user.
     * You should then use the access_token in the url and new password to update the using:
     *
     * See [updateUser], example usage:
     *
     * ```
     * goTrueClient.updateUser(
     *      jwt = accessToken,
     *      attributes = GoTrueUserAttributes(
     *          password = "newPassword"
     *      )
     * )
     * ```
     *
     * @param[email] The email address of the user.
     */
    fun resetPasswordForEmail(email: String) {
        goTrueHttpClient.post(
            url = "/recover",
            data = mapOf("email" to email)
        )
    }

    /**
     * Updates the user data.
     * Apart from changing email/password, this method can be used to set custom user data.
     *
     * @param[jwt] A valid, logged-in JWT.
     * @param[attributes] Custom user attributes you want to update
     */
    fun updateUser(jwt: String, attributes: GoTrueUserAttributes): UserResponseClass {
        val response = goTrueHttpClient.put(
            url = "/user",
            headers = mapOf("Authorization" to "Bearer $jwt"),
            data = mapOf(
                "email" to attributes.email,
                "password" to attributes.password,
                "data" to attributes.data
            )
        )

        return goTrueJsonConverter.deserialize(response, goTrueUserResponseClass)
    }

    /**
     * Gets the user details.
     *
     * @param[jwt] A valid, logged-in JWT.
     */
    fun getUser(jwt: String): UserResponseClass {
        val response = goTrueHttpClient.get(
            url = "/user",
            headers = mapOf("Authorization" to "Bearer $jwt")
        )

        return goTrueJsonConverter.deserialize(response, goTrueUserResponseClass)
    }

    /**
     * Logs in an existing user using their [email] address.
     *
     * @param[email] The email address of the user.
     * @param[password] The password of the user.
     */
    fun signInWithEmail(email: String, password: String): TokenResponseClass {
        val response = goTrueHttpClient.post(
            url = "/token?grant_type=password",
            data = mapOf("email" to email, "password" to password),
        )!!

        return goTrueJsonConverter.deserialize(response, goTrueTokenResponseClass)
    }

    /**
     * Generates a new JWT.
     *
     * @param[refreshToken] A valid refresh token that was returned on login.
     */
    fun refreshAccessToken(refreshToken: String): TokenResponseClass {
        val response = goTrueHttpClient.post(
            url = "/token?grant_type=refresh_token",
            data = mapOf("refresh_token" to refreshToken),
        )!!

        return goTrueJsonConverter.deserialize(response, goTrueTokenResponseClass)
    }

    /**
     * Removes a logged-in session.
     *
     * This will revoke all refresh tokens for the user.
     * Remember that the JWT tokens will still be valid for stateless auth until they expire.
     *
     * @param[jwt] A valid, logged-in JWT.
     */
    fun signOut(jwt: String) {
        goTrueHttpClient.post(
            url = "/logout",
            headers = mapOf("Authorization" to "Bearer $jwt")
        )
    }

    /**
     * Sends a magic login (passwordless) link to an [email] address.
     *
     * @param[email] The email address of the user.
     */
    fun sendMagicLinkEmail(email: String) {
        goTrueHttpClient.post(
            url = "/magiclink",
            data = mapOf("email" to email)
        )
    }
}
