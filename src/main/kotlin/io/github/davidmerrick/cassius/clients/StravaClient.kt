package io.github.davidmerrick.cassius.clients

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.davidmerrick.cassius.config.StravaConfig
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.server.exceptions.HttpServerException
import mu.KotlinLogging
import javax.inject.Singleton

private const val STRAVA_BASE_URL = "https://www.strava.com/api/v3"
private const val ACTIVITY_ENDPOINT = "/activities"
private const val AUTH_ENDPOINT = "/oauth/token"

private val log = KotlinLogging.logger {}

@Singleton
class StravaClient(
        @Client(STRAVA_BASE_URL) private val client: RxHttpClient,
        private val config: StravaConfig
) {

    /**
     * Returns the raw JSON string of the activity
     */
    fun getActivity(activityId: Long): String {
        val token = getAccessToken()
        val request = HttpRequest
                .GET<String>("$ACTIVITY_ENDPOINT/$activityId")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}")
        log.info("Fetching activity $activityId from Strava")
        val response = client
                .exchange(request, String::class.java)
                .blockingFirst()
        if (response.status != HttpStatus.OK) {
            log.error("Strava returned ${response.status}: ${response.body}")
            throw HttpServerException("Failed to retrieve activity from Strava")
        }
        return response.body()!!
    }

    /**
     * Returns auth token.
     * For now, given low load, refresh every time.
     * Future: Caffeine cache backed by Firestore.
     */
    private fun getAccessToken(): StravaToken {
        log.info("Retrieving Strava access token")

        val request = HttpRequest
                .POST(AUTH_ENDPOINT, null)

        request.parameters.add("client_id", config.clientId)
                .add("client_secret", config.clientSecret)
                .add("grant_type", "refresh_token")
                .add("refresh_token", config.refreshToken)

        val response = client
                .exchange(request, StravaToken::class.java)
                .blockingFirst()
        if (response.status != HttpStatus.OK) {
            log.error("Strava auth call returned ${response.status}: ${response.body()}")
            throw HttpServerException("Failed to authenticate with Strava")
        }
        return response.body()!!
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StravaToken(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("expires_at")
        val expiresAt: Long
)