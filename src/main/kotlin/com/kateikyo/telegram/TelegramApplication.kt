package com.kateikyo.telegram

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TelegramApplication

fun main(args: Array<String>) {
	runApplication<TelegramApplication>(*args)

	val logger = LoggerFactory.getLogger(TelegramApplication::class.java)

	if (logger.isDebugEnabled) {
		System.getenv().filter { it.key.startsWith("YANDEX_") || it.key.startsWith("BOT_") }
			.forEach { logger.debug("ENV: '{}={}'", it.key, it.value) }
	}
}
