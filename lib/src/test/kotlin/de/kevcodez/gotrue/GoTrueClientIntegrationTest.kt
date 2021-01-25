package de.kevcodez.gotrue

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GoTrueClientIntegrationTest {

    private var wireMockServer: WireMockServer = WireMockServer(9911)

    private var goTrueClient: GoTrueClient? = null

    @BeforeEach
    fun proxyToWireMock() {
        wireMockServer.start()
        goTrueClient = GoTrueDefaultClient(
                baseUrl = "http://localhost:${wireMockServer.port()}",
                defaultHeaders = emptyMap()
        )
    }

    @AfterEach
    fun noMoreWireMock() {
        wireMockServer.stop()
        wireMockServer.resetAll()
    }

    @Nested
    inner class Settings {

        @Test
        fun `should get settings`() {
            wireMockServer.stubFor(
                    get("/settings").willReturn(aResponse()
                            .withStatus(200)
                            .withBody(fixture("/fixtures/get-settings.json")))
            )

            val settings = goTrueClient!!.settings()

            assertAll {
                with(settings) {
                    assertThat(autoconfirm).isTrue()
                    assertThat(disableSignup).isFalse()
                    assertThat(external.bitbucket).isFalse()
                    assertThat(external.github).isFalse()
                    assertThat(external.gitlab).isFalse()
                }
            }
        }
    }

    private fun fixture(path: String): String {
        return GoTrueClientIntegrationTest::class.java.getResource(path).readText()
    }

}