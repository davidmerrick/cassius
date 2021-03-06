package io.github.davidmerrick.cassius.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.davidmerrick.cassius.TestApplication
import io.github.davidmerrick.cassius.clients.StravaClient
import io.github.davidmerrick.cassius.storage.ActivityLoader
import io.github.davidmerrick.cassius.storage.ActivityStorage
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

private const val EVENTS_ENDPOINT = "/strava/events"
private const val BACKFILL_ENDPOINT = "/strava/activities/bulk"

@MicronautTest(application = TestApplication::class)
class StravaControllerTest {
    @get:MockBean(ActivityStorage::class)
    val activityStorage = mockk<ActivityStorage>()

    @get:MockBean(ActivityLoader::class)
    val activityLoader = mockk<ActivityLoader>()

    @get:MockBean(StravaClient::class)
    val stravaClient = mockk<StravaClient>()

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var mapper: ObjectMapper

    @BeforeEach
    fun beforeMethod() {
        clearMocks(stravaClient, activityStorage)
        every {
            activityStorage.createActivity(any(), any())
        } just runs

        every {
            activityLoader.loadActivity(any())
        } just runs

        every {
            stravaClient.getActivity(any())
        } returns "hello world"
    }

    @Test
    fun `Reject challenge if hub verify token doesn't match`() {
        val request = HttpRequest.GET<String>(EVENTS_ENDPOINT)
        request.parameters.add(HUB_MODE, "foo")
        request.parameters.add(HUB_CHALLENGE, "bar")
        request.parameters.add(HUB_VERIFY_TOKEN, "badBadToken")

        try {
            client.toBlocking().retrieve(request, HttpStatus::class.java)
        } catch (e: HttpClientResponseException) {
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
    fun `Post update event should return 200`() {
        val payload = mapOf(
                "aspect_type" to "update",
                "event_time" to "1549560669",
                "object_id" to "0000000000",
                "object_type" to "activity",
                "owner_id" to "9999999",
                "subscription_id" to "999999",
                "updates" to mapOf("title" to "Messy")
        )
        val request = HttpRequest.POST(
                EVENTS_ENDPOINT,
                mapper.writeValueAsString(payload)
        )

        val status = client.toBlocking().retrieve(request, HttpStatus::class.java)
        status shouldBe HttpStatus.OK
    }

    @Test
    fun `Post create event should return 200`() {
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

    @Test
    fun `Bulk activities endpoint should store multiple activities`() {
        val payload = listOf(12345L, 99999L)

        val request = HttpRequest.POST(
                BACKFILL_ENDPOINT,
                mapper.writeValueAsString(payload)
        )

        client.toBlocking().retrieve(request, HttpStatus::class.java)
        verify(exactly = 2) { stravaClient.getActivity(any()) }
    }
}