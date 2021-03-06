package io.github.davidmerrick.cassius.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("gcp")
class GoogleCloudConfig {

    @get:NotBlank
    lateinit var bucketName: String
}