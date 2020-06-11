package io.github.davidmerrick.strava.controllers

import io.github.davidmerrick.strava.TestApplication
import io.kotlintest.shouldBe
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest(application = TestApplication::class)
class StravaControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `Strava activities endpoint stubbed out`() {
        val request = HttpRequest.POST("/strava/activities", "banana")

        val response = client.toBlocking().retrieve(request)
        response.contains("hello world") shouldBe true
    }
}