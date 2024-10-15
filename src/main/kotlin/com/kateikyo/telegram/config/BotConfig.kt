package com.kateikyo.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("bot")
class BotConfig(
    var token: String = "token1",
    var userOwnerId: Long = 0,
    var groupFiltering: GroupFiltering = GroupFiltering(),
    var usersFiltering: UsersFiltering = UsersFiltering(),
    var manyAttachmentsMessage: String = "empty"
)