package io.github.davidmerrick.cassius.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("auth0")
class Auth0Config {
    @get:NotBlank
    lateinit var domain: String

    @get:NotBlank
    lateinit var clientId: String

    @get:NotBlank
    lateinit var clientSecret: String
}