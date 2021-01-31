package io.supabase.gotrue

import io.supabase.gotrue.http.GoTrueHttpClient
import io.supabase.gotrue.http.GoTrueHttpClientApache
import io.supabase.gotrue.json.GoTrueJsonConverter
import io.supabase.gotrue.json.GoTrueJsonConverterJackson
import org.apache.hc.client5.http.impl.classic.HttpClients

val goTrueJsonConverter = GoTrueJsonConverterJackson()

/**
 * The default client uses Apache HTTP client 5.x and Jackson FasterXML for DTO conversion.
 *
 * If you want to customize, implement [GoTrueHttpClient] and [GoTrueJsonConverter].
 */
class GoTrueDefaultClient(
        url: String,
        headers: Map<String, Any>
) : GoTrueClient(
        goTrueHttpClient = GoTrueHttpClientApache(
                url = url,
                headers = headers,
                httpClient = { HttpClients.createDefault() },
                goTrueJsonConverter = goTrueJsonConverter
        ),
        goTrueJsonConverter = goTrueJsonConverter
)