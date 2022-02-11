package io.supabase.gotrue

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.supabase.gotrue.types.CustomGoTrueUserResponse
import io.supabase.gotrue.types.GoTrueTokenResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CustomGoTrueClientIntegrationTest {

    private var wireMockServer: WireMockServer = WireMockServer(0)

    private var goTrueClient: GoTrueClient<CustomGoTrueUserResponse, GoTrueTokenResponse>? = null

    @BeforeEach
    fun proxyToWireMock() {
        wireMockServer.start()
        goTrueClient = GoTrueClient.customApacheJacksonGoTrueClient(
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
    fun `should sign up when email confirmation disabled`() {
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
                        .withBody(fixture("/fixtures/user-response-email-disabled.json"))
                )
        )

        val response = goTrueClient!!.signUpWithEmail(
            email = "foo@bar.de",
            password = "foobar"
        )
        println(response)
    }

    private fun fixture(path: String): String {
        return CustomGoTrueClientIntegrationTest::class.java.getResource(path).readText()
    }
}
