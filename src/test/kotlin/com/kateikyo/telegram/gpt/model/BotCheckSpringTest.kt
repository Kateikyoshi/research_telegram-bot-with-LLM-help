package com.kateikyo.telegram.gpt.model

import com.kateikyo.telegram.BotCheck
import com.kateikyo.telegram.gpt.YandexGptClient
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

@ActiveProfiles("test")
//or apply BOT_GROUPFILTERING_DISABLE=true;BOT_USERSFILTERING_DISABLE=true by editing test run configuration
@SpringBootTest(properties = ["BOT.GROUPFILTERING.DISABLE=true", "BOT.USERSFILTERING.DISABLE=true"])
class BotCheckSpringTest {

    @Autowired
    private lateinit var botCheck: BotCheck

    @MockBean
    private lateinit var yandexGptClientMock: YandexGptClient

    @Suppress("unused")
    @MockBean
    private lateinit var botsApiMock: TelegramBotsApi

    @Test
    fun checkDisabledModerationFlow() {

        val userMock: User = mock {
            on { userName } doReturn "name1"
        }
        val chatMock: Chat = mock {
            on { type } doReturn "channel"
            on { title } doReturn "title1"
        }
        val messageMock: Message = mock {
            on { forwardFromChat } doReturn chatMock
            on { from } doReturn userMock
        }
        val updateMock: Update = mock {
            on { this.message } doReturn messageMock
        }

        /* starting */
        botCheck.onUpdateReceived(updateMock)

        /* verifying */
        verify(updateMock, atLeastOnce()).chatJoinRequest
        verify(updateMock, atLeastOnce()).message
        verify(messageMock, atLeastOnce()).chatId
        verify(yandexGptClientMock, atLeastOnce()).getLlmReply("name1", "title1")
    }
}