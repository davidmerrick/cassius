package io.github.davidmerrick.cassius.storage

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import io.github.davidmerrick.cassius.config.GoogleCloudConfig
import io.micronaut.http.MediaType
import mu.KotlinLogging
import javax.inject.Singleton


private val log = KotlinLogging.logger {}

@Singleton
class ActivityStorage(private val config: GoogleCloudConfig) {

    private val client = StorageOptions
            .getDefaultInstance()
            .service

    /**
     * Stores raw Strava activity payload
     */
    fun createActivity(activityId: Long, activity: String) {
        log.info("Writing activity $activityId payload to bucket")
        val blobId = BlobId.of(config.bucketName, "$activityId.json")
        val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(MediaType.APPLICATION_JSON)
                .build()
        client.create(blobInfo, activity.toByteArray())
    }
}