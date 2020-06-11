package io.github.davidmerrick.strava.controllers

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/strava")
class StravaController {

    @Post("/activities")
    fun handleEvent(@Body message: String): String {
        return "hello world"
    }
}