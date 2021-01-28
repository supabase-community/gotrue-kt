package io.supabase.gotrue.json

/**
 * Interface used by the GoTrueClient, allows replacing the default JSON converter.
 *
 * Overwrite it to replace the default Jackson FasterXML implementation.
 */
interface GoTrueJsonConverter {

    /**
     * Serializes [data] as JSON string.
     *
     * @param[data] the data to serialize
     *
     * @return JSON string
     */
    fun serialize(data: Any): String

    /**
     * Deserializes a JSON [text] to the corresponding [responseType].
     *
     * @param[text] The JSON text to convert
     * @param[responseType] The response type as Java class
     */
    fun <T : Any> deserialize(text: String, responseType: Class<T>): T
}

inline fun <reified T : Any> GoTrueJsonConverter.deserialize(content: String): T = deserialize(content, T::class.java)