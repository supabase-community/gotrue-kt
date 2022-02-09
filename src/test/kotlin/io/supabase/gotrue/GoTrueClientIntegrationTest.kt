package io.supabase.gotrue

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.supabase.gotrue.types.GoTrueTokenResponse
import io.supabase.gotrue.types.GoTrueUserAttributes
import io.supabase.gotrue.types.GoTrueUserResponse
import io.supabase.gotrue.types.GoTrueVerifyType
import org.apache.hc.core5.http.HttpHeaders
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GoTrueClientIntegrationTest {

    private var wireMockServer: WireMockServer = WireMockServer(0)

    private var goTrueClient: GoTrueClient<GoTrueUserResponse, GoTrueTokenResponse>? = null

    @BeforeEach
    fun proxyToWireMock() {
        wireMockServer.start()
        goTrueClient = GoTrueClient.defaultGoTrueClient(
            url = "http://localhost:${wireMockServer.port()}",
            headers = emptyMap()
        )
    }

    @AfterEach
    fun noMoreWireMock() {
        wireMockServer.stop()
        wireMockServer.resetAll()
    }

    @Test
    fun `should get settings`() {
        wireMockServer.stubFor(
            get("/settings").willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(fixture("/fixtures/settings-response.json"))
            )
        )

        goTrueClient!!.settings()
    }

    @Test
    fun `should sign up`() {
        wireMockServer.stubFor(
            post("/signup")
                .withRequestBody(
                    equalToJson(
                        """{
                           "email": "foo@bar.de", 
                           "password": "foobar"
                           }
                        """
                    )
                )
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/user-response.json"))
                )
        )

        goTrueClient!!.signUpWithEmail(
            email = "foo@bar.de",
            password = "foobar"
        )
    }

    @Test
    fun `should invite`() {
        wireMockServer.stubFor(
            post("/invite")
                .withRequestBody(equalToJson("""{"email": "foo@bar.de"}"""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/user-response.json"))
                )
        )

        goTrueClient!!.inviteUserByEmail("foo@bar.de")
    }

    @Test
    fun `should verify`() {
        wireMockServer.stubFor(
            post("/verify")
                .withRequestBody(
                    equalToJson(
                        """{
                           "type": "recovery", 
                           "token": "123"
                           }
                        """
                    )
                )
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/token-response.json"))
                )
        )

        goTrueClient!!.verify(
            type = GoTrueVerifyType.RECOVERY,
            token = "123"
        )
    }

    @Test
    fun `should recover`() {
        wireMockServer.stubFor(
            post("/recover")
                .withRequestBody(equalToJson("""{"email": "foo@bar.de"}"""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )

        goTrueClient!!.resetPasswordForEmail("foo@bar.de")
    }

    @Test
    fun `should update user`() {
        wireMockServer.stubFor(
            put("/user")
                .withRequestBody(
                    equalToJson(
                        """{
                           "data": {
                            "admin": true
                           }
                       }
                        """
                    )
                )
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/user-response.json"))
                )
        )

        goTrueClient!!.updateUser(jwt = "token", attributes = GoTrueUserAttributes(data = mapOf("admin" to true)))
    }

    @Test
    fun `should get user`() {
        wireMockServer.stubFor(
            get("/user")
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/user-response.json"))
                )
        )

        goTrueClient!!.getUser("token")
    }

    @Test
    fun `should refresh access token`() {
        wireMockServer.stubFor(
            post("/token?grant_type=refresh_token")
                .withRequestBody(
                    equalToJson(
                        """{
                           "refresh_token": "refreshToken" 
                           }
                        """
                    )
                )
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/token-response.json"))
                )
        )

        goTrueClient!!.refreshAccessToken("refreshToken")
    }

    @Test
    fun `should issue token with email and password`() {
        wireMockServer.stubFor(
            post("/token?grant_type=password")
                .withRequestBody(
                    equalToJson(
                        """{
                           "email": "foo@bar.de", 
                           "password": "pw"
                           }
                        """
                    )
                )
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(fixture("/fixtures/token-response.json"))
                )
        )

        goTrueClient!!.signInWithEmail("foo@bar.de", "pw")
    }

    @Test
    fun `should sign out user`() {
        wireMockServer.stubFor(
            post("/logout")
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )

        goTrueClient!!.signOut("token")
    }

    @Test
    fun `should send magic link`() {
        wireMockServer.stubFor(
            post("/magiclink")
                .withRequestBody(equalToJson("""{"email": "foo@bar.de"}"""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )

        goTrueClient!!.sendMagicLinkEmail("foo@bar.de")
    }

    private fun fixture(path: String): String {
        return GoTrueClientIntegrationTest::class.java.getResource(path).readText()
    }
}
