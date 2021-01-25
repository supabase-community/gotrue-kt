package de.kevcodez.gotrue

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.StringEntity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class GoTrueHttpClientTest {

    private val baseUrl = "https://test.com"
    private val httpClientMock = mockk<CloseableHttpClient>()
    private val defaultHeaders = mapOf(
            "Authorization" to "Bearer foobar"
    )

    private val goTrueHttpClient = GoTrueHttpClient(
            baseUrl = baseUrl,
            defaultHeaders = defaultHeaders,
            httpClient = httpClientMock
    )

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ThrowHttpException {

        @ParameterizedTest
        @MethodSource("throwHttpException")
        fun `should throw http exception when status is above 300`() {
            val httpResponse = mockk<CloseableHttpResponse>()
            val responseCode = 301
            val httpBody = "hello"

            every { httpResponse.code } returns responseCode
            every { httpResponse.entity } returns StringEntity(httpBody)

            mockHttpCall(httpResponse)

            val exception = assertThrows<GoTrueHttpException> {
                goTrueHttpClient.get(
                        path = "/anywhere",
                        responseType = String::class
                )
            }

            assertThat(exception.status).isEqualTo(responseCode)
            assertThat(exception.httpBody).isEqualTo(httpBody)
        }

        @Suppress("unused")
        private fun throwHttpException(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(301, "httpbody"),
                    Arguments.of(301, null),
                    Arguments.of(400, "httpbody")
            )
        }
    }

    private fun mockHttpCall(httpResponse: CloseableHttpResponse) {
        every { httpClientMock.close() }.returns(Unit)

        every { httpClientMock.execute(any(), any<HttpClientResponseHandler<Any>>()) }.answers {
            val handler = args[1] as HttpClientResponseHandler<Any>
            handler.handleResponse(httpResponse)
        }

    }

}