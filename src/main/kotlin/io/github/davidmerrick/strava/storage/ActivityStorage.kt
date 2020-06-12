package io.github.davidmerrick.strava.storage

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import io.github.davidmerrick.strava.config.GoogleCloudConfig
import javax.inject.Singleton


@Singleton
class ActivityStorage(private val config: GoogleCloudConfig) {

    private val client = StorageOptions
            .getDefaultInstance()
            .service

    /**
     * Stores raw Strava activity payload
     */
    fun createActivity(activityId: Long, activity: String) {
        val blobId = BlobId.of(config.bucketName, "$activityId.json")
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build()
        client.create(blobInfo, activity.toByteArray())
    }
}