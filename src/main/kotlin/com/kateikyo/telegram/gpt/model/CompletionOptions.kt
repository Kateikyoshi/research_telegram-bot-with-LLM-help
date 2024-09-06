package com.kateikyo.telegram.gpt.model

data class CompletionOptions(
    var stream: Boolean = false,
    var temperature: Int = 1,
    var maxTokens: String = "50"
)