package io.github.davidmerrick.cassius.storage

import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.FormatOptions
import io.github.davidmerrick.cassius.config.BigQueryConfig
import io.github.davidmerrick.cassius.config.GoogleCloudConfig
import mu.KotlinLogging
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

@Singleton
class ActivityLoader(
        private val bigQueryConfig: BigQueryConfig,
        private val googleCloudConfig: GoogleCloudConfig
) {
    private val client = BigQueryOptions
            .getDefaultInstance()
            .service

    /**
     * Loads activity into BigQuery
     */
    fun loadActivity(activityId: Long) {
        log.info("Loading $activityId into BigQuery")

        val sourceUri = "gs://${googleCloudConfig.bucketName}/$activityId.json"
        client.getTable(bigQueryConfig.datasetName, bigQueryConfig.tableName)
                .load(FormatOptions.json(), sourceUri)
    }

}