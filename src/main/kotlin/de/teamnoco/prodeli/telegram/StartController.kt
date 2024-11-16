package de.teamnoco.prodeli.telegram

import de.teamnoco.prodeli.service.InviteService
import de.teamnoco.prodeli.service.UserService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.internal.MessageUpdate
import org.springframework.stereotype.Component
import eu.vendeli.tgbot.types.User as TgUser

@Component
class StartController(
    private val inviteService: InviteService,
    private val userService: UserService
) {

    @CommandHandler(["/start"])
    suspend fun start(user: TgUser, bot: TelegramBot, update: MessageUpdate) {
        if (update.message.text != null && update.message.text!!.startsWith("/start ")) {
            val arg = update.message.text!!.removePrefix("/start ")
            if (arg.startsWith("invite")) {
                val inviteId = arg.removePrefix("invite")
                inviteService.applyInvite(userService.getUserById(user.id) ?: return, inviteId)
            }
        }

        message { "Привет! Чтобы воспользоваться ботом, запускай приложение \uD83D\uDC47" }
            .inlineKeyboardMarkup { "Запустить" webAppInfo "https://prodeli.teamnoco.de/" }
            .send(user, bot)
    }

}