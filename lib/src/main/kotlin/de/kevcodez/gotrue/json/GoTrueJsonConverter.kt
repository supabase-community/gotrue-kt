package de.kevcodez.gotrue.json

import kotlin.reflect.KClass

interface GoTrueJsonConverter {

    fun serialize(data: Any): String

    fun <T : Any> deserialize(str: String, responseType: KClass<T>): T
}