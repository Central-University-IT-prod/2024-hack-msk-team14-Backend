package de.teamnoco.prodeli

import de.teamnoco.prodeli.config.ApplicationConfig
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@ImportAutoConfiguration(ApplicationConfig::class)
class ProdeliApplication

fun main(args: Array<String>) {
    runApplication<ProdeliApplication>(*args)
}
