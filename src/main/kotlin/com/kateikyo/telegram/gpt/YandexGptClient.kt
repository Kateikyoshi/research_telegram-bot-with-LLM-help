package com.kateikyo.telegram.gpt

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.kateikyo.telegram.config.YandexGptConfig
import com.kateikyo.telegram.gpt.model.YandexGptRequest
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class YandexGptClient(
    private val webClient: WebClient,
    private val yandexGptConfig: YandexGptConfig
) {

    fun getLlmReply(username: String, channelName: String): String {
        val request = YandexGptRequest()
        request.modelUri = request.modelUri.replace(FOLDER_ID_PLACEHOLDER, yandexGptConfig.folderId)
        request.messages[1].text = request.messages[1].text.replace(CHANNEL_NAME_PLACEHOLDER, channelName)

        return runBlocking {
            webClient.post()
                .uri(yandexGptConfig.url)
                .headers { it.set("Authorization", "${yandexGptConfig.authType} ${yandexGptConfig.key}") }
                .bodyValue(request)
                .retrieve()
                .awaitBody<JsonNode>()
        }.extractLlmReply()
    }

    private fun JsonNode.extractLlmReply(): String {
        val alternativesNode = this.get("result")?.get("alternatives") as ArrayNode
        return alternativesNode.get(0)?.get("message")?.get("text")?.textValue()
            ?: throw IllegalStateException("LLM reply can't be null")
    }

    companion object {
        const val CHANNEL_NAME_PLACEHOLDER = "<channelName>"
        @Suppress("unused")
        const val USERNAME_PLACEHOLDER = "<user>"
        const val FOLDER_ID_PLACEHOLDER = "<folder-id>"
    }
}