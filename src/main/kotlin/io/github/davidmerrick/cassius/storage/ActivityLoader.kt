package io.github.davidmerrick.cassius.storage

import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.FormatOptions
import com.google.cloud.bigquery.JobInfo
import com.google.cloud.bigquery.LoadJobConfiguration
import com.google.cloud.bigquery.TableId
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
    private val client by lazy {
        val builder = BigQueryOptions
                .getDefaultInstance()
                .toBuilder()

        bigQueryConfig.projectId?.let {
            builder.setProjectId(it)
        }

        builder.build().service
    }

    /**
     * Loads activity into BigQuery
     */
    fun loadActivity(activityId: Long) {
        log.info("Loading $activityId into BigQuery")

        val sourceUri = "gs://${googleCloudConfig.bucketName}/$activityId.json"
        try {
            val activitiesTable = TableId
                    .of(bigQueryConfig.datasetName, bigQueryConfig.tableName)
            
            val jobConfig = LoadJobConfiguration.builder(activitiesTable, sourceUri)
                    .setFormatOptions(FormatOptions.json())
                    .setIgnoreUnknownValues(true)
                    .build()

            val jobFuture = client.create(JobInfo.of(jobConfig))
            log.info("Created job with id ${jobFuture.jobId}")
        } catch (e: Exception) {
            // Log and continue
            log.error("Exception thrown while loading activity $activityId into BigQuery", e)
        }
    }

}