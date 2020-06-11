package io.github.davidmerrick.strava

import io.micronaut.runtime.Micronaut

object TestApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .mainClass(Application.javaClass)
                .environments("test")
                .start()
    }
}