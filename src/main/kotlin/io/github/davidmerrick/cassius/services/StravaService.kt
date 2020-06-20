package io.github.davidmerrick.cassius.services

import io.github.davidmerrick.cassius.clients.StravaClient
import io.github.davidmerrick.cassius.storage.ActivityLoader
import io.github.davidmerrick.cassius.storage.ActivityStorage
import io.micronaut.caffeine.cache.Caffeine
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

private const val MAX_CACHE_SIZE = 100L
private const val CACHE_TTL_MINUTES = 15L

@Singleton
class StravaService(
        private val storage: ActivityStorage,
        private val client: StravaClient,
        private val activityLoader: ActivityLoader
) {
    private val activityIdCache = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterWrite(CACHE_TTL_MINUTES, TimeUnit.MINUTES)
            .build<Long, Boolean>()

    /**
     * Fetches activity from Strava and stores it
     */
    fun processActivity(activityId: Long) {
        // Skip fetch if activity is in cache
        val isCached = activityIdCache.asMap().putIfAbsent(activityId, true) ?: false
        if (isCached) {
            log.info("Activity $activityId in cache. Skipping fetch.")
            return
        }

        // Fetch activity from Strava
        log.info("Fetching activity $activityId from Strava")
        val activity = client.getActivity(activityId)

        // Write activity to bucket
        log.info("Writing activity payload to bucket")
        storage.createActivity(activityId, activity)

        // Load activity
        log.info("Loading activity into data warehouse")
        activityLoader.loadActivity(activityId)
    }
}