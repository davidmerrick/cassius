package io.github.davidmerrick.cassius.services

import io.github.davidmerrick.cassius.TestApplication
import io.github.davidmerrick.cassius.clients.StravaClient
import io.github.davidmerrick.cassius.storage.ActivityLoader
import io.github.davidmerrick.cassius.storage.ActivityStorage
import io.kotlintest.shouldBe
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest(application = TestApplication::class)
class StravaServiceTest {

    private val slot = slot<Long>()

    @get:MockBean(StravaClient::class)
    val stravaClient = mockk<StravaClient>()

    @get:MockBean(ActivityLoader::class)
    val activityLoader = mockk<ActivityLoader>()

    @get:MockBean(ActivityStorage::class)
    val activityStorage = mockk<ActivityStorage>()

    @Inject
    lateinit var service: StravaService

    @BeforeEach
    fun beforeMethod(){
        slot.clear()
        clearMocks(stravaClient, activityStorage)
        every {
            stravaClient.getActivity(capture(slot))
        } returns "Hello world"

        every {
            activityStorage.createActivity(any(), any())
        } just runs

        every {
            activityLoader.loadActivity(any())
        } just runs
    }

    @Test
    fun `Activity cache test`(){
        val id = 12345L
        service.processActivity(id)
        slot.isCaptured shouldBe true
        slot.clear()

        // Second call, activity should be cached
        // so Strava won't be called
        service.processActivity(id)
        slot.isCaptured shouldBe false
    }
}