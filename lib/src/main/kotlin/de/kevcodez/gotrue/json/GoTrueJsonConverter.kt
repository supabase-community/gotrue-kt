package de.kevcodez.gotrue.json

interface GoTrueJsonConverter {

    fun serialize(data: Any): String

    fun <T : Any> deserialize(str: String, responseType: Class<T>): T
}

inline fun <reified T : Any> GoTrueJsonConverter.deserialize(content: String): T = deserialize(content, T::class.java)