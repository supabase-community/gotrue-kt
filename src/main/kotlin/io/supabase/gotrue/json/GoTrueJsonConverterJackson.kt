package io.supabase.gotrue.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

/**
 * Default implementation of the [GoTrueJsonConverter] used by the GoTrueDefaultClient.
 *
 * Uses Jackson FasterXML for JSON (de)-serialization.
 */
class GoTrueJsonConverterJackson : GoTrueJsonConverter {

    private val objectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun serialize(data: Any): String {
        return objectMapper.writeValueAsString(data)
    }

    override fun <T : Any> deserialize(text: String, responseType: Class<T>): T {
        return objectMapper.readValue(text, responseType)
    }
}
