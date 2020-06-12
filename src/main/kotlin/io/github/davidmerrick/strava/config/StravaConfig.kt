package io.github.davidmerrick.strava.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("strava")
class StravaConfig {
    var challengeVerifyToken: String? = null
}