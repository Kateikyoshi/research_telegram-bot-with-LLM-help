package com.kateikyo.telegram

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Suppress("unused")
@Configuration
class SpringAppConfig {

    @Bean
    fun objectMapper() = jacksonObjectMapper()

    @Bean
    fun webClient() = WebClient.builder().build()

    @Bean
    fun botApi(): TelegramBotsApi {
        return TelegramBotsApi(DefaultBotSession::class.java)
    }
}