package de.kevcodez.gotrue

import de.kevcodez.gotrue.http.GoTrueHttpClientApache
import de.kevcodez.gotrue.json.GoTrueJsonConverterJackson
import org.apache.hc.client5.http.impl.classic.HttpClients

val goTrueJsonConverter = GoTrueJsonConverterJackson()

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