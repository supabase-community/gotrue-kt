package io.supabase.gotrue.http

import io.supabase.gotrue.json.GoTrueJsonConverter
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import java.net.URI

class GoTrueHttpClientApache(
        private val baseUrl: String,
        private val defaultHeaders: Map<String, Any>,
        private val httpClient: CloseableHttpClient,
        private val goTrueJsonConverter: GoTrueJsonConverter
): GoTrueHttpClient {

    override fun post(path: String, headers: Map<String, String>, data: Any?): String? {
        return execute(
                method = Method.POST,
                path = path,
                headers = headers,
                data = data
        )
    }

    override fun put(path: String, headers: Map<String, String>, data: Any): String {
        return execute(
                method = Method.PUT,
                path = path,
                headers = headers,
                data = data
        )!!
    }

    override fun get(path: String, headers: Map<String, String>): String {
        return execute(
                method = Method.GET,
                path = path,
                headers = headers
        )!!
    }

    private fun execute(method: Method, path: String, data: Any? = null, headers: Map<String, String> = emptyMap()): String? {
        return httpClient.use { httpClient ->
            val httpRequest = HttpUriRequestBase(method.name, URI(baseUrl + path))
            data?.apply {
                val dataAsString = goTrueJsonConverter.serialize(data)
                httpRequest.entity = StringEntity(dataAsString)
            }
            val allHeaders = defaultHeaders.filter { !headers.containsKey(it.key) } + headers
            allHeaders.forEach { (name, value) -> httpRequest.addHeader(name, value) }

            return@use httpClient.execute(httpRequest, responseHandler())
        }
    }

    private fun responseHandler(): HttpClientResponseHandler<String?> {
        return HttpClientResponseHandler<String?> { response ->
            throwIfError(response)

            return@HttpClientResponseHandler response.entity?.let { EntityUtils.toString(it) }
        }
    }

    private fun throwIfError(response: ClassicHttpResponse) {
        val status = response.code
        val statusSuccessful = status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION

        if (!statusSuccessful) {
            val entityAsString = response.entity?.let { EntityUtils.toString(it) }

            throw GoTrueHttpException(status, entityAsString)
        }
    }
}