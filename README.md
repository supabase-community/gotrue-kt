# Kotlin Client for GoTrue

Kotlin client for [Netlify's GoTrue API](https://github.com/netlify/gotrue).

Comes with DTOs for the responses to enable type-safe access.

## Usage

```kotlin
val goTrueClient = GoTrueClient(
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
    println("Oops, status: ${exc.status}, body:\n${exc.httpBody}")
}
```

If you are using supabase, the base URL will be `https://<your-project-id>.supabase.co/auth/v1`

## Internal libs

The Apache Http-Client (5.x) is used for executing HTTP calls, Jackson is used to convert responses to DTOs.
If people actually need it, one may allow both to be interchangeable.