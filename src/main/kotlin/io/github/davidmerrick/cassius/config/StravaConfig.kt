package io.github.davidmerrick.cassius.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("strava")
class StravaConfig {
    var challengeVerifyToken: String? = null
}