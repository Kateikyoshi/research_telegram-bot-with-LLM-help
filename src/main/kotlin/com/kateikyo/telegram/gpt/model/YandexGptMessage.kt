package com.kateikyo.telegram.gpt.model

import com.kateikyo.telegram.gpt.YandexGptClient.Companion.CHANNEL_NAME_PLACEHOLDER

data class YandexGptMessage(
    var role: String = "",
    var text: String = ""
) {
    companion object {
        val DEFAULT_MESSAGE1 = YandexGptMessage(
            role = "system",
            text = "В одно предложение, очень кратко и строго ответьте, что здесь не филиал новостников"
        )
        val DEFAULT_MESSAGE2 = YandexGptMessage(
            role = "user",
            text = "Переслали из $CHANNEL_NAME_PLACEHOLDER"
        )
    }
}

