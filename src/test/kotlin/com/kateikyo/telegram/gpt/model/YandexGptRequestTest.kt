package com.kateikyo.telegram.gpt.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kateikyo.telegram.gpt.YandexGptClient.Companion.CHANNEL_NAME_PLACEHOLDER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class YandexGptRequestTest{


    @Test
    fun testMapping() {

        val mapper = jacksonObjectMapper()

        val yandexGptRequest = YandexGptRequest()

        val requestTree = """
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

        val tree1 = mapper.valueToTree<JsonNode>(yandexGptRequest)
        assertThat(tree1).isNotNull
        val tree2 = mapper.readTree(requestTree)
        assertThat(tree2).isNotNull
        assertThat(tree1).isEqualTo(tree2)
    }
}