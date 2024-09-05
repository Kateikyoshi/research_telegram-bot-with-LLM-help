package com.kateikyo.telegram

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.time.Instant

@Component
class BotCheck(
    private val botConfig: BotConfig,
    private val yandexGptClient: YandexGptClient
) : TelegramLongPollingBot(botConfig.token) {

    private val botsApi: TelegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)

    @PostConstruct
    fun notifyOwnerAboutStarting() {
        botsApi.registerBot(this)

        this.sendText(botConfig.userOwnerId, "My backend starts, it is ${Instant.now()}")
    }

    override fun getBotUsername(): String {
        return BOT_USERNAME_CONST
    }

    override fun onUpdateReceived(update: Update?) {

        if (update == null) {
            logger.info("Update object is null, aborting")
            return
        }

        if (update.chatJoinRequest != null) {
            logger.info("It is a chat join request, aborting")
            return
        }

        val message = update.message
        if (message == null) {
            logger.info("Update object is null, aborting")
            return
        }

        logger.info("chat id: ${message.chatId}")

        if (message.isForwardedFromChannel()
            && botConfig.groupIdsToReplyTo.contains(message.chatId)
        ) {
            if (botConfig.usersToModerate.contains(message.from.userName)) {
                val llmReply = yandexGptClient.getLlmReply(message.from.userName, message.forwardFromChat.title)

                sendText(message.chatId, "@${message.from.userName} $llmReply")
            }
        } else if (message.isUserMessage) {
            sendText(message.from.id, "Don't talk to me, peasant")
        }
    }

    private fun Message.isForwardedFromChannel(): Boolean {
        val forwardFromChat = this.forwardFromChat ?: return false
        return forwardFromChat.type == "channel"
    }

    private fun Message.isTextual(): Boolean {
        return this.text != null
    }

    private fun Message.isForwarded(): Boolean {
        return this.forwardFrom != null
    }

    private fun sendText(victim: Long?, text: String?) {

        if (victim == null) {
            throw IllegalArgumentException("victim can't be null")
        }
        if (text == null) {
            throw IllegalArgumentException("text can't be null")
        }

        val sendMessage = SendMessage.builder()
            .chatId(victim)
            .text(text)
            .build()

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            logger.info("An ex!: ${e.message}")
        }

    }

    companion object {
        private val logger = LoggerFactory.getLogger(BotCheck::class.java)

        private const val BOT_USERNAME_CONST = "kateikyoshi_bot"
    }
}