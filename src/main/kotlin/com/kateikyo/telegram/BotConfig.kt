package com.kateikyo.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("bot")
class BotConfig(
    var token: String = "",
    var userOwnerId: Long = 0,
    var groupIdsToReplyTo: List<Long> = emptyList(),
    var usersToModerate: List<String> = emptyList()
)