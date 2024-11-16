package de.teamnoco.prodeli.telegram

import de.teamnoco.prodeli.data.event.repository.EventRepository
import de.teamnoco.prodeli.data.user.repository.UserRepository
import de.teamnoco.prodeli.service.DebtService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.message
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.time.Instant
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Component
class NotificationTimer(
    val botInstances: MutableList<TelegramBot>,
    private val debtService: DebtService,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {

    @Scheduled(fixedRate = 600000, initialDelay = 600000)
    fun notificationTimer() {
        runBlocking {
            eventRepository.findAll().forEach {
                if (it.lockedDate == null) return@runBlocking
                if (it.lockedDate!! + 3.days.toJavaDuration() < Instant.now()) return@runBlocking
                it.members.forEach { member ->
                    val debts = debtService.getDebtInfo(member).debts
                    debts.forEach { (lenderId, debtInfo) ->
                        if (debtInfo.youOwe > BigInteger.ZERO) return@runBlocking
                        val lender = userRepository.findById(lenderId).getOrNull() ?: return@runBlocking
                        botInstances.forEach { bot ->
                            message { "Напоминаем, что вы должны пользователю ${lender.firstName} ${lender.lastName} ${debtInfo.youOwe} рублей в событии ${it.name}!" }.send(
                                member.userId,
                                bot
                            )
                        }
                    }
                }
            }
        }
    }
}