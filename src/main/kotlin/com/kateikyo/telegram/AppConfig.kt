package com.kateikyo.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
    }

    @Bean
    fun webClient() = WebClient.builder().build()
}