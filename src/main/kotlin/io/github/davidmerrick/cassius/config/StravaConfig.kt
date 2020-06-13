package io.github.davidmerrick.cassius.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("strava")
class StravaConfig {
    var challengeVerifyToken: String? = null

    @get:NotBlank
    lateinit var clientId: String

    @get:NotBlank
    lateinit var clientSecret: String

    @get:NotBlank
    lateinit var refreshToken: String
}