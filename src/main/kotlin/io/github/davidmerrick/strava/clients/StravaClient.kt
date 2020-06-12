package io.github.davidmerrick.strava.clients

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import javax.inject.Singleton

private const val BASE_API_PATH = "foo"

@Singleton
class StravaClient(
        @Client(BASE_API_PATH) private val client: HttpClient
) {

    /**
     * Returns the raw JSON string of the activity
     */
    fun getActivity(actividyId: Long): String {
        return "banana"
    }
}