package io.supabase.gotrue

import io.supabase.gotrue.http.GoTrueHttpClientApache
import io.supabase.gotrue.http.GoTrueHttpClient
import io.supabase.gotrue.json.GoTrueJsonConverterJackson
import io.supabase.gotrue.json.GoTrueJsonConverter
import org.apache.hc.client5.http.impl.classic.HttpClients

val goTrueJsonConverter = GoTrueJsonConverterJackson()

/**
 * The default client uses Apache HTTP client 5.x and Jackson FasterXML for DTO conversion.
 *
 * If you want to customize, implement [GoTrueHttpClient] and [GoTrueJsonConverter].
 */
class GoTrueDefaultClient(
        baseUrl: String,
        defaultHeaders: Map<String, Any>
) : GoTrueClient(
        goTrueHttpClient = GoTrueHttpClientApache(
                baseUrl = baseUrl,
                defaultHeaders = defaultHeaders,
                httpClient = HttpClients.createDefault(),
                goTrueJsonConverter = goTrueJsonConverter
        ),
        goTrueJsonConverter = goTrueJsonConverter
)