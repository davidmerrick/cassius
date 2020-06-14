package io.github.davidmerrick.cassius.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.davidmerrick.cassius.config.StravaConfig
import io.github.davidmerrick.cassius.models.StravaAspectType.CREATE
import io.github.davidmerrick.cassius.models.StravaObjectType.ACTIVITY
import io.github.davidmerrick.cassius.models.StravaWebhookEvent
import io.github.davidmerrick.cassius.services.StravaService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import mu.KotlinLogging

internal const val HUB_MODE = "hub.mode"
internal const val HUB_CHALLENGE = "hub.challenge"
internal const val HUB_VERIFY_TOKEN = "hub.verify_token"

private val log = KotlinLogging.logger {}

@Controller("/strava")
class StravaController(
        private val config: StravaConfig,
        private val service: StravaService,
        private val mapper: ObjectMapper
) {
    /**
     * When subscribing to webhooks, Strava will do a GET request to the endpoint with
     * challenge params.
     * https://developers.strava.com/docs/webhooks/
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/events")
    fun getChallenge(
            @QueryValue(HUB_MODE) hubMode: String,
            @QueryValue(HUB_CHALLENGE) hubChallenge: String,
            @QueryValue(HUB_VERIFY_TOKEN) hubVerifyToken: String
    ): ChallengeResponse {
        log.info("Received Strava challenge: $hubChallenge")
        config.challengeVerifyToken?.let {
            if (hubVerifyToken != it) {
                throw HttpClientResponseException("Invalid $HUB_VERIFY_TOKEN value", HttpResponse.badRequest<String>())
            }
        }
        return ChallengeResponse(hubChallenge)
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/events")
    fun handleEvent(@Body payload: StravaWebhookEvent): HttpResponse<String> {
        log.debug("Received webhook payload: ${mapper.writeValueAsString(payload)}")

        // Filter only created activities
        if (payload.aspectType != CREATE || payload.objectType != ACTIVITY) {
            return HttpResponse.ok()
        }

        service.processActivity(payload.objectId)

        return HttpResponse.ok()
    }
}

data class ChallengeResponse(
        @JsonProperty(HUB_CHALLENGE)
        val hubChallenge: String
)