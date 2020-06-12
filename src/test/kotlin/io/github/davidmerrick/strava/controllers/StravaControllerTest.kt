package io.github.davidmerrick.strava.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.davidmerrick.strava.TestApplication
import io.github.davidmerrick.strava.storage.ActivityStorage
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import javax.inject.Inject

private const val EVENTS_ENDPOINT = "/strava/events"

@MicronautTest(application = TestApplication::class)
class StravaControllerTest {
    @get:MockBean(ActivityStorage::class)
    val activityStorage = mockk<ActivityStorage>()

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var mapper: ObjectMapper

    init {
        every {
            activityStorage.createActivity(any(), any())
        } just runs
    }

    @Test
    fun `Reject challenge if hub verify token doesn't match`() {
        val request = HttpRequest.GET<String>(EVENTS_ENDPOINT)
        request.parameters.add(HUB_MODE, "foo")
        request.parameters.add(HUB_CHALLENGE, "bar")
        request.parameters.add(HUB_VERIFY_TOKEN, "badBadToken")

        try {
            client.toBlocking().retrieve(request, HttpStatus::class.java)
        } catch (e: HttpClientResponseException){
            return
        }
        fail("Should have thrown HttpClientResponseException")
    }

    @Test
    fun `Strava challenge endpoint should return challenge`() {
        val request = HttpRequest.GET<String>(EVENTS_ENDPOINT)
        request.parameters.add(HUB_MODE, "foo")
        request.parameters.add(HUB_CHALLENGE, "bar")
        request.parameters.add(HUB_VERIFY_TOKEN, "banana")

        val response = client.toBlocking().retrieve(request)
        response.contains("hub.challenge") shouldBe true
        response.contains("bar") shouldBe true
    }

    @Test
    fun `Post a valid webhook event should return 200`() {
        val payload = mapOf(
                "aspect_type" to "create",
                "event_time" to "1549560669",
                "object_id" to "0000000000",
                "object_type" to "activity",
                "owner_id" to "9999999",
                "subscription_id" to "999999"
        )
        val request = HttpRequest.POST(
                EVENTS_ENDPOINT,
                mapper.writeValueAsString(payload)
        )

        val status = client.toBlocking().retrieve(request, HttpStatus::class.java)
        status shouldBe HttpStatus.OK
    }
}