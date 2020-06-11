package io.github.davidmerrick.strava.controllers

import io.github.davidmerrick.strava.TestApplication
import io.kotlintest.shouldBe
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject

private const val EVENTS_ENDPOINT = "/strava/events"

@MicronautTest(application = TestApplication::class)
class StravaControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `Strava activities endpoint stubbed out`() {
        val request = HttpRequest.POST(EVENTS_ENDPOINT, "banana")

        val response = client.toBlocking().retrieve(request)
        response.contains("hello world") shouldBe true
    }

    @Test
    fun `Strava challenge endpoint should return challenge`() {
        val request = HttpRequest.GET<String>(EVENTS_ENDPOINT)
        request.parameters.add(HUB_MODE, "foo")
        request.parameters.add(HUB_CHALLENGE, "bar")
        request.parameters.add(HUB_VERIFY_TOKEN, "baz")

        val response = client.toBlocking().retrieve(request)
        response.contains("hub.challenge") shouldBe true
        response.contains("bar") shouldBe true
    }
}