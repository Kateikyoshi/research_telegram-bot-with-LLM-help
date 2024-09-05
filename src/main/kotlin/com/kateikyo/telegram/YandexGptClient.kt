package com.kateikyo.telegram

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class YandexGptClient(
    private val webClient: WebClient,
    private val yandexGptConfig: YandexGptConfig,
    private val mapper: ObjectMapper
) {

    fun getLlmReply(username: String, channelName: String): String {
        val requestNode = mapper.readTree(requestTree).replacePlaceholders(username, channelName)

        return runBlocking {
            webClient.post()
                .uri(yandexGptConfig.url)
                .headers { it.set("Authorization", "${yandexGptConfig.authType} ${yandexGptConfig.key}") }
                .bodyValue(requestNode)
                .retrieve()
                .awaitBody<JsonNode>()
        }.extractLlmReply()
    }

    private fun JsonNode.extractLlmReply(): String {
        val alternativesNode = this.get("result")?.get("alternatives") as ArrayNode
        return alternativesNode.get(0)?.get("message")?.get("text")?.textValue()
            ?: throw IllegalStateException("LLM reply can't be null")
    }

    private fun JsonNode.replacePlaceholders(username: String, channelName: String): JsonNode {
        val thisObjectNode = this as? ObjectNode ?: throw IllegalStateException("requestTree is null?")
        val modelUriNode =
            thisObjectNode.get("modelUri") ?: throw IllegalStateException("modelUri of requestTree is null?")

        thisObjectNode.set<JsonNode>(
            "modelUri",
            TextNode(modelUriNode.textValue().replace("<folder-id>", yandexGptConfig.folderId))
        )

        val messagesArrayNode =
            thisObjectNode.get("messages") as? ArrayNode ?: throw IllegalStateException("messagesArrayNode is null?")
        val messagesArr1 = messagesArrayNode[1] ?: throw IllegalStateException("messagesArray1 is null?")
        val textNode = messagesArr1.get("text") ?: throw IllegalStateException("text node of messages is null?")
        (messagesArr1 as ObjectNode).set<JsonNode>(
            "text",
            TextNode(textNode.textValue().replace(CHANNEL_NAME_PLACEHOLDER, channelName))
        )

        return thisObjectNode
    }

    companion object {
        private const val CHANNEL_NAME_PLACEHOLDER = "<channelName>"
        private const val USERNAME_PLACEHOLDER = "<user>"

        private val requestTree = """
            {
              "modelUri": "gpt://<folder-id>/yandexgpt-lite",
              "completionOptions": {
                "stream": false,
                "temperature": 1,
                "maxTokens": "30"
              },
              "messages": [
                {
                  "role": "system",
                  "text": "В одно предложение, очень кратко и строго ответьте, что здесь не филиал новостников"
                },
                {
                  "role": "user",
                  "text": "Переслали из $CHANNEL_NAME_PLACEHOLDER"
                }
              ]
            }
        """.trimIndent()
    }
}