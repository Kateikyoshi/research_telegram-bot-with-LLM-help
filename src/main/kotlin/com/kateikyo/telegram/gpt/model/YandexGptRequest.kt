package com.kateikyo.telegram.gpt.model

import com.kateikyo.telegram.gpt.YandexGptClient.Companion.FOLDER_ID_PLACEHOLDER

data class YandexGptRequest(
    var modelUri: String = "gpt://$FOLDER_ID_PLACEHOLDER/yandexgpt-lite",
    var completionOptions: CompletionOptions = CompletionOptions(),
    var messages: List<YandexGptMessage> = listOf(YandexGptMessage.DEFAULT_MESSAGE1, YandexGptMessage.DEFAULT_MESSAGE2)
)