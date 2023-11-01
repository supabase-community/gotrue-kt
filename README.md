# Kotlin Client for GoTrue

> **Warning**
> This repository is archived. Use [supabase-kt](https://github.com/supabase-community/supabase-kt) instead to use Supabase in your Kotlin projects.


Kotlin JVM client for [Netlify's GoTrue API](https://github.com/netlify/gotrue).

Comes with DTOs for the responses to enable type-safe access.

![Java CI with Gradle](https://img.shields.io/github/workflow/status/supabase/gotrue-kt/Java%20CI%20with%20Gradle?label=BUILD&style=for-the-badge)
![Gradle Package](https://img.shields.io/github/workflow/status/supabase/gotrue-kt/Gradle%20Package?label=PUBLISH&style=for-the-badge)
![Bintray](https://img.shields.io/bintray/v/supabase/supabase/gotrue-kt?style=for-the-badge)

## Installation

Maven

```xml
<dependency>
    <groupId>io.supabase</groupId>
    <artifactId>gotrue-kt</artifactId>
    <version>{version}</version>
    <type>pom</type>
</dependency>
```

Gradle

```groovy
implementation 'io.supabase:gotrue-kt:{version}'
```

## Usage

```kotlin
val goTrueClient = GoTrueClient.defaultGoTrueClient(
    url = "<base-url>",
    headers = mapOf("Authorization" to "foo", "apiKey" to "bar")
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

You can also customize the DTO for example if you turn off email verification

```kotlin
data class CustomGoTrueUserResponse(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val user: User
)

data class User(
    val id: UUID,
    val email: String,
    val phone: String

)

GoTrueClient.customApacheJacksonGoTrueClient<CustomGoTrueUserResponse, GoTrueTokenResponse>(
    url = "<base-url>",
    headers = mapOf("Authorization" to "foo", "apiKey" to "bar")
)
```

If you are using [supabase](https://supabase.io/), the base URL will be `https://<your-project-id>.supabase.co/auth/v1`

## HTTP / (De)-Serialization

The Apache Http-Client (5.x) is used for executing HTTP calls, Jackson is used to convert responses to DTOs.

If you want to change that, you need to implement the `GoTrueHttpClient` and the `GoTrueJsonConverter` interface.

See [GoTrueHttpClientApache](src/main/kotlin/io/supabase/gotrue/http/GoTrueHttpClientApache.kt) and [GoTrueJsonConverterJackson](src/main/kotlin/io/supabase/gotrue/json/GoTrueJsonConverterJackson.kt).

```kotlin
GoTrueClient.goTrueClient<GoTrueUserResponse,GoTrueTokenResponse>(
    goTrueHttpClient = { customHttpClient() },
    goTrueJsonConverter = customConverter()
)
```
