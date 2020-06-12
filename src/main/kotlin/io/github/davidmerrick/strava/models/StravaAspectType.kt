package io.github.davidmerrick.strava.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class StravaAspectType {
    @JsonProperty("create")
    CREATE,
    @JsonProperty("update")
    UPDATE,
    @JsonProperty("delete")
    DELETE
}