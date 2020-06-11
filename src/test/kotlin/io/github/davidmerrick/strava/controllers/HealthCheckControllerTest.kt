package io.github.davidmerrick.strava.controllers

import io.github.davidmerrick.strava.TestApplication
import io.kotlintest.shouldBe
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest(application = TestApplication::class)
class HealthCheckControllerTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `Health check endpoint should be enabled`() {
        val response = client.toBlocking().retrieve("/health")
        response.contains("status") shouldBe true
        response.contains("UP") shouldBe true
    }
}