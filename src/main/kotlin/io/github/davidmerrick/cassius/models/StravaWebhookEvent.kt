package io.github.davidmerrick.cassius.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

data class StravaWebhookEvent(
        @JsonProperty("object_type")
        val objectType: StravaObjectType,
        @JsonProperty("object_id")
        val objectId: Long,
        @JsonProperty("aspect_type")
        val aspectType: StravaAspectType,
        val updates: JsonNode?,
        @JsonProperty("owner_id")
        val ownerId: Long, // Athlete's id
        @JsonProperty("subscription_id")
        val subscriptionId: Int
)