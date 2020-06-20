package io.github.davidmerrick.cassius.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("bigQuery")
class BigQueryConfig {

    @get:NotBlank
    lateinit var tableName: String

    @get:NotBlank
    lateinit var datasetName: String
}