package io.github.davidmerrick.strava.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.davidmerrick.strava.clients.StravaClient
import io.github.davidmerrick.strava.models.StravaAspectType.CREATE
import io.github.davidmerrick.strava.models.StravaObjectType
import io.github.davidmerrick.strava.models.StravaObjectType.ACTIVITY
import io.github.davidmerrick.strava.models.StravaWebhookEvent
import io.github.davidmerrick.strava.storage.ActivityStorage
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import mu.KotlinLogging

internal const val HUB_MODE = "hub.mode"
internal const val HUB_CHALLENGE = "hub.challenge"
internal const val HUB_VERIFY_TOKEN = "hub.verify_token"

private val log = KotlinLogging.logger {}

@Controller("/strava/events")
class StravaController(
        private val stravaClient: StravaClient,
        private val storage: ActivityStorage
) {

    /**
     * When subscribing to webhooks, Strava will do a GET request to the endpoint with
     * challenge params.
     * https://developers.strava.com/docs/webhooks/
     */
    @Get
    fun getChallenge(
            @QueryValue(HUB_MODE) hubMode: String,
            @QueryValue(HUB_CHALLENGE) hubChallenge: String,
            @QueryValue(HUB_VERIFY_TOKEN) hubVerifyToken: String
    ): ChallengeResponse {
        log.info("Received Strava challenge: $hubChallenge")
        return ChallengeResponse(hubChallenge)
    }

    @Post
    fun handleEvent(@Body payload: StravaWebhookEvent): HttpResponse<String> {
        // Filter only created activities
        if (payload.aspectType != CREATE || payload.objectType != ACTIVITY) {
            return HttpResponse.ok()
        }

        // Fetch activity from Strava
        val activity = stravaClient.getActivity(payload.objectId)

        // Write activity to bucket
        storage.createActivity(activity)

        return HttpResponse.ok()
    }
}

data class ChallengeResponse(
        @JsonProperty(HUB_CHALLENGE)
        val hubChallenge: String
)