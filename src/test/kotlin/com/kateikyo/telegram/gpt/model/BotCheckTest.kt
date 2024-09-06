package com.kateikyo.telegram.gpt.model

import com.kateikyo.telegram.BotCheck
import com.kateikyo.telegram.config.BotConfig
import com.kateikyo.telegram.config.GroupFiltering
import com.kateikyo.telegram.config.UsersFiltering
import com.kateikyo.telegram.gpt.YandexGptClient
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.mockito.kotlin.mock
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class BotCheckTest {

    @Test
    fun checkDisabledModerationFlow() {

        val yandexGptClientMock: YandexGptClient = mock()
        val botCheck = BotCheck(
            botConfig = BotConfig(
                token = "token",
                userOwnerId = 1,
                groupFiltering = GroupFiltering(
                    disable = true,
                    groupIdsToReplyTo = emptyList()
                ),
                usersFiltering = UsersFiltering(
                    disable = true,
                    usersToModerate = emptyList()
                )
            ),
            yandexGptClient = yandexGptClientMock,
            botsApi = mock()
        )

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