package com.kateikyo.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("yandex.gpt")
class YandexGptConfig(
    var url: String = "",
    var key: String = "",
    var authType: String = "",
    var folderId: String = ""
)