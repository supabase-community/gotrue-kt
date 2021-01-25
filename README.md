# Kotlin Client for GoTrue

Kotlin JVM client for [Netlify's GoTrue API](https://github.com/netlify/gotrue).

Comes with DTOs for the responses to enable type-safe access.

![Java CI with Gradle](https://img.shields.io/github/workflow/status/kevcodez/gotrue-kt/Java%20CI%20with%20Gradle?label=BUILD&style=for-the-badge)
![Gradle Package](https://img.shields.io/github/workflow/status/kevcodez/gotrue-kt/Gradle%20Package?label=PUBLISH&style=for-the-badge)
![Bintray](https://img.shields.io/bintray/v/kevcodez/gotrue-kt/gotrue-kt?style=for-the-badge)

## Installation

Maven
```xml
<dependency>
	<groupId>de.kevcodez</groupId>
	<artifactId>gotrue-kt</artifactId>
	<version>0.0.3</version>
	<type>pom</type>
</dependency>
```

Gradle
```groovy
implementation 'de.kevcodez:gotrue-kt:0.0.3'
```


## Usage

```kotlin
val goTrueClient = GoTrueDefaultClient(
        baseUrl = "<base-url>",
        defaultHeaders = mapOf("Authorization" to "foo", "apiKey" to "bar")
)

try {
    goTrueClient.invite("e@ma.il")

    val updatedUser = goTrueClient.updateUser(
            accessToken = "eyJ...", // read from request header
            data = mapOf(
                    "admin" = true
            )
    )
    
    println(updatedUser.updatedAt)
} catch (exc: GoTrueHttpException) {
    // Exception is thrown on bad status (anything above 300)
    println("Oops, status: ${exc.status}, body:\n${exc.httpBody}")
}
```

If you are using [supabase](https://supabase.io/), the base URL will be `https://<your-project-id>.supabase.co/auth/v1`

## HTTP / (De)-Serialization

The Apache Http-Client (5.x) is used for executing HTTP calls, Jackson is used to convert responses to DTOs.

If you want to change that, you need to implement the `GoTrueHttpClient` and the `GoTrueJsonConverter` interface.

See [GoTrueHttpClientApache](lib/src/main/kotlin/de/kevcodez/gotrue/http/GoTrueHttpClientApache.kt) and [GoTrueJsonConverterJackson](lib/src/main/kotlin/de/kevcodez/gotrue/json/GoTrueJsonConverterJackson.kt).

```kotlin
val goTrueClient = GoTrueClient(
        goTrueHttpClient = customHttpClient(),
        goTrueJsonConverter = customConverter()
)
```