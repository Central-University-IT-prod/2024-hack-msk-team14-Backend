package de.teamnoco.prodeli.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@Configuration
@PropertySource("classpath:application.yaml")
class ApplicationConfig