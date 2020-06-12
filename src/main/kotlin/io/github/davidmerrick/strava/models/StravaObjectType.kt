package io.github.davidmerrick.strava.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class StravaObjectType {
    @JsonProperty("activity")
    ACTIVITY,
    @JsonProperty("athlete")
    ATHLETE
}