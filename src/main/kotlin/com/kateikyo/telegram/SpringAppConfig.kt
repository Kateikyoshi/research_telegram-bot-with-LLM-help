package com.kateikyo.telegram

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Suppress("unused")
@Configuration
class SpringAppConfig {

    @Bean
    fun objectMapper() = jacksonObjectMapper()

    @Bean
    fun webClient() = WebClient.builder().build()
}