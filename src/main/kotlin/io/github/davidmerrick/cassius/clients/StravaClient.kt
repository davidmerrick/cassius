package io.github.davidmerrick.cassius.clients

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.davidmerrick.cassius.config.StravaConfig
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.exceptions.HttpException
import io.micronaut.http.server.exceptions.HttpServerException
import mu.KotlinLogging
import javax.inject.Singleton

private const val STRAVA_BASE_URL = "https://www.strava.com/api/v3"
private const val ACTIVITY_ENDPOINT = "/activities"
private const val AUTH_ENDPOINT = "/oauth/token"

private val log = KotlinLogging.logger {}

@Singleton
class StravaClient(
        @Client(STRAVA_BASE_URL) private val client: HttpClient,
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
        return try {
            client.toBlocking().retrieve(request)
        } catch (e: HttpException) {
            log.error("Failed to fetch activity from Strava", e)
            throw HttpServerException("Failed to retrieve activity")
        }
    }

    /**
     * Returns auth token.
     * For now, given low load, refresh every time.
     * Future: Caffeine cache backed by Firestore.
     */
    private fun getAccessToken(): StravaToken {
        log.info("Retrieving Strava access token")

        val request = HttpRequest
                .POST(AUTH_ENDPOINT, "")

        request.parameters.add("client_id", config.clientId)
        request.parameters.add("client_secret", config.clientSecret)
        request.parameters.add("grant_type", "refresh_token")
        request.parameters.add("refresh_token", config.refreshToken)

        return try {
            client.toBlocking()
                    .retrieve(request, StravaToken::class.java)
        } catch (e: HttpException) {
            log.error("Strava auth call failed", e)
            throw HttpServerException("Failed to authenticate with Strava")
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StravaToken(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("expires_at")
        val expiresAt: Long
)