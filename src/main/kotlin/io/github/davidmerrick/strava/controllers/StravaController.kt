package io.github.davidmerrick.strava.controllers

import com.fasterxml.jackson.annotation.JsonProperty
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
class StravaController {

    /**
     * When subscribing to webhooks, Strava will do a GET request to the endpoint with
     * challenge params.
     * https://developers.strava.com/docs/webhooks/
     */
    @Get
    fun handleChallenge(
            @QueryValue(HUB_MODE) hubMode: String,
            @QueryValue(HUB_CHALLENGE) hubChallenge: String,
            @QueryValue(HUB_VERIFY_TOKEN) hubVerifyToken: String
    ): ChallengeResponse {
        log.info("Received Strava challenge: $hubChallenge")
        return ChallengeResponse(hubChallenge)
    }

    @Post
    fun handleEvent(@Body message: String): String {
        return "hello world"
    }
}

data class ChallengeResponse(
        @JsonProperty(HUB_CHALLENGE)
        val hubChallenge: String
)