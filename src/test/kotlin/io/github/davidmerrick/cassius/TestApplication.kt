package io.github.davidmerrick.cassius

import io.github.davidmerrick.cassius.Application
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