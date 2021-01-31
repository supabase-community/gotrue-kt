package io.supabase.gotrue

import io.supabase.gotrue.http.GoTrueHttpClient
import io.supabase.gotrue.json.GoTrueJsonConverter
import io.supabase.gotrue.json.deserialize
import io.supabase.gotrue.types.*

open class GoTrueClient(
        private val goTrueHttpClient: GoTrueHttpClient,
        private val goTrueJsonConverter: GoTrueJsonConverter
) {

    /**
     * @return the publicly available settings for this GoTrue instance.
     */
    fun settings(): GoTrueSettings {
        val response = goTrueHttpClient.get(
                url = "/settings"
        )

        return goTrueJsonConverter.deserialize(response)
    }

    /**
     * Creates a new user using their [email] address.
     *
     * @param[email] The email address of the user.
     * @param[password] The password of the user.
     */
    fun signUpWithEmail(email: String, password: String): GoTrueUserResponse {
        val response = goTrueHttpClient.post(
                url = "/signup",
                data = mapOf("email" to email, "password" to password)
        )!!

        return goTrueJsonConverter.deserialize(response)
    }

    /**
     * Sends an invite link to an [email] address.
     *
     * @param[email] The email address of the user.
     */
    fun inviteUserByEmail(email: String): GoTrueUserResponse {
        val response = goTrueHttpClient.post(
                url = "/invite",
                data = mapOf("email" to email)
        )!!

        return goTrueJsonConverter.deserialize(response)
    }

    /**
     * Verify a registration or a password recovery.
     * Type can be signup or recovery and the token is a token returned from either /signup or /recover.
     */
    fun verify(type: GoTrueVerifyType, token: String, password: String? = null): GoTrueTokenResponse {
        val response = goTrueHttpClient.post(
                url = "/verify",
                data = mapOf("type" to type.name.toLowerCase(), "token" to token, "password" to password),
        )!!

        return goTrueJsonConverter.deserialize(response)
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
    fun updateUser(jwt: String, attributes: GoTrueUserAttributes): GoTrueUserResponse {
        val response = goTrueHttpClient.put(
                url = "/user",
                headers = mapOf("Authorization" to "Bearer $jwt"),
                data = mapOf(
                        "email" to attributes.email,
                        "password" to attributes.password,
                        "data" to attributes.data
                )
        )

        return goTrueJsonConverter.deserialize(response)
    }

    /**
     * Gets the user details.
     *
     * @param[jwt] A valid, logged-in JWT.
     */
    fun getUser(jwt: String): GoTrueUserResponse {
        val response = goTrueHttpClient.get(
                url = "/user",
                headers = mapOf("Authorization" to "Bearer $jwt")
        )

        return goTrueJsonConverter.deserialize(response)
    }

    /**
     * Logs in an existing user using their [email] address.
     *
     * @param[email] The email address of the user.
     * @param[password] The password of the user.
     */
    fun signInWithEmail(email: String, password: String): GoTrueTokenResponse {
        val response = goTrueHttpClient.post(
                url = "/token?grant_type=password",
                data = mapOf("email" to email, "password" to password),
        )!!

        return goTrueJsonConverter.deserialize(response)
    }

    /**
     * Generates a new JWT.
     *
     * @param[refreshToken] A valid refresh token that was returned on login.
     */
    fun refreshAccessToken(refreshToken: String): GoTrueTokenResponse {
        val response = goTrueHttpClient.post(
                url = "/token?grant_type=refresh_token",
                data = mapOf("refresh_token" to refreshToken),
        )!!

        return goTrueJsonConverter.deserialize(response)
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
