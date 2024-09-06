package com.kateikyo.telegram

import com.kateikyo.telegram.config.BotConfig
import com.kateikyo.telegram.gpt.YandexGptClient
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class BotCheck(
    private val botConfig: BotConfig,
    private val yandexGptClient: YandexGptClient,
    private val botsApi: TelegramBotsApi
) : TelegramLongPollingBot(botConfig.token) {

    @PostConstruct
    fun notifyOwnerAboutStarting() {
        botsApi.registerBot(this)

        if (logger.isDebugEnabled) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
            this.sendText(botConfig.userOwnerId, "My backend starts, it is ${formatter.format(Instant.now())}")
        }
    }

    override fun getBotUsername(): String {
        return BOT_USERNAME_CONST
    }

    override fun onUpdateReceived(update: Update?) {

        if (update == null) {
            logger.debug("Update object is null, aborting")
            return
        }

        if (update.chatJoinRequest != null) {
            logger.debug("It is a chat join request, aborting")
            return
        }

        val message = update.message
        if (message == null) {
            logger.debug("Update object is null, aborting")
            return
        }

        logger.debug("General info: chat id: {}, isForwarded: {}, username: {}, forwardFromChat title: {}",
            message.chatId, message.isForwardedFromChannel(), message.from.userName, message.forwardFromChat?.title)
        logger.debug("Group filtering disabled: {}, user filtering: {}",
            botConfig.groupFiltering.disable, botConfig.usersFiltering.disable)

        if (message.isForwardedFromChannel()
            && (botConfig.groupFiltering.disable || botConfig.groupFiltering.groupIdsToReplyTo.contains(message.chatId))
        ) {
            if (botConfig.usersFiltering.disable || botConfig.usersFiltering.usersToModerate.contains(message.from.userName)) {
                consultLlmAndSend(message)
            }
        } else if (message.isUserMessage) {
            sendTextReply(message, "Don't talk to me, peasant")
        }
    }

    private fun consultLlmAndSend(message: Message) {
        if (message.from.userName != null && message.from.userName.isNotBlank()) {
            val llmReply = yandexGptClient.getLlmReply(message.from.userName, message.forwardFromChat.title)
            sendTextWithPing(message, llmReply)
        } else if (message.from.firstName.isNotBlank()) {
            val llmReply = yandexGptClient.getLlmReply(message.from.firstName, message.forwardFromChat.title)
            sendTextReply(message, llmReply)
        }
    }

    private fun Message.isForwardedFromChannel(): Boolean {
        val forwardFromChat = this.forwardFromChat ?: return false
        return forwardFromChat.type == "channel"
    }

    /**
     * userName can be null, so this is not the best reply method
     */
    private fun sendTextWithPing(message: Message, whatToSay: String) {

        if (message.chatId == null) {
            throw IllegalArgumentException("victim can't be null")
        }
        if (message.from.userName == null) {
            throw IllegalArgumentException("You can't ping null nicknames")
        }

        val sendMessage = SendMessage.builder()
            .chatId(message.chatId)
            .text("@${message.from.userName} $whatToSay")
            .build()

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            logger.info("sendTextWithPing ex!: ", e)
        }
    }

    /**
     * Safe, since firstName is never null
     */
    private fun sendTextReply(message: Message, whatToSay: String) {

        if (message.chatId == null) {
            throw IllegalArgumentException("victim can't be null")
        }

        val sendMessage = SendMessage.builder()
            .replyToMessageId(message.messageId)
            .chatId(message.chatId)
            .text("${message.from.firstName}, $whatToSay")
            .build()

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            logger.info("sendTextReply ex!: ", e)
        }
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