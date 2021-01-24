package de.kevcodez.gotrue

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.hc.client5.http.ClientProtocolException
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import java.net.URI
import kotlin.reflect.KClass

class HttpClient(
        private val baseUrl: String,
        private val defaultHeaders: Map<String, Any>
) {
    private val objectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun <T : Any> post(path: String, responseType: KClass<T>? = null, headers: Map<String, String> = emptyMap(), data: Any? = null): T {
        return execute(
                method = Method.POST,
                path = path,
                responseType = responseType,
                headers = headers,
                data = data
        )
    }

    fun <T : Any> put(path: String, responseType: KClass<T>? = null, headers: Map<String, String> = emptyMap(), data: Any): T {
        return execute(
                method = Method.POST,
                path = path,
                responseType = responseType,
                headers = headers,
                data = data
        )
    }

    fun <T : Any> get(path: String, responseType: KClass<T>, headers: Map<String, String> = emptyMap()): T {
        return execute(
                method = Method.GET,
                path = path,
                responseType = responseType,
                headers = headers
        )
    }

    private fun <T : Any> execute(method: Method, path: String, responseType: KClass<T>? = null, data: Any? = null, headers: Map<String, String> = emptyMap()): T {
        val httpClient = HttpClients.createDefault()

        return httpClient.use { httpClient ->
            val httpRequest = HttpUriRequestBase(method.name, URI(baseUrl + path))
            if (data != null) {
                val dataAsString = objectMapper.writeValueAsString(data)
                httpRequest.entity = StringEntity(dataAsString)
            }
            defaultHeaders.filter { !headers.containsKey(it.key) }.forEach { (name, value) -> httpRequest.addHeader(name, value) }
            headers.forEach { (name, value) -> httpRequest.addHeader(name, value) }
            return@use httpClient.execute(httpRequest, responseHandler(responseType?.java))
        }
    }

    private fun <T> responseHandler(responseType: Class<T>?): HttpClientResponseHandler<T> {
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
                    return if (entityAsString == null || responseType == null)
                        null
                    else
                        objectMapper.readValue(entityAsString, responseType)
                } else {
                    throw GoTrueHttpException(status, entityAsString);
                }
            }
        }
    }
}