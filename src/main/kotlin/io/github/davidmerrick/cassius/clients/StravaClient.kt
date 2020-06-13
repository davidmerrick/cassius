package io.github.davidmerrick.cassius.clients

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.davidmerrick.cassius.config.StravaConfig
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import mu.KotlinLogging
import javax.inject.Singleton

private const val STRAVA_HOST = "https://www.strava.com"
private const val ACTIVITY_ENDPOINT = "/v3/activities"
private const val AUTH_ENDPOINT = "/oauth/token"

private val log = KotlinLogging.logger {}

@Singleton
class StravaClient(
        @Client(STRAVA_HOST) private val client: HttpClient,
        private val config: StravaConfig
) {

    /**
     * Returns the raw JSON string of the activity
     */
    fun getActivity(actividyId: Long): String {
        val token = getAccessToken()
        val request = HttpRequest
                .GET<String>("$ACTIVITY_ENDPOINT/$actividyId")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}")
        return client.toBlocking().retrieve(request)
    }

    /**
     * Returns auth token.
     * For now, given low load, refresh every time.
     * Future: Caffeine cache backed by Firestore.
     */
    private fun getAccessToken(): StravaToken {
        log.info("Fetching Strava access token")

        val request = HttpRequest
                .POST(AUTH_ENDPOINT, "")

        request.parameters.add("client_id", config.clientId)
        request.parameters.add("client_secret", config.clientSecret)
        request.parameters.add("grant_type", "refresh_token")
        request.parameters.add("refresh_token", config.refreshToken)

        return client.toBlocking().retrieve(request, StravaToken::class.java)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StravaToken(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("expires_in")
        val expiresIn: Long
)