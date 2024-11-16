package de.teamnoco.prodeli.service

import de.teamnoco.prodeli.data.debt.dto.DebtInfoResponse
import de.teamnoco.prodeli.data.event.dto.EventCreateRequest
import de.teamnoco.prodeli.data.event.dto.EventUpdateRequest
import de.teamnoco.prodeli.data.event.exception.EventNotFoundException
import de.teamnoco.prodeli.data.event.model.Event
import de.teamnoco.prodeli.data.event.model.EventMember
import de.teamnoco.prodeli.data.event.repository.EventMemberRepository
import de.teamnoco.prodeli.data.event.repository.EventRepository
import de.teamnoco.prodeli.data.user.exception.UserNotFoundException
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.web.response.StatusResponse
import de.teamnoco.prodeli.web.response.respondOk
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.message
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.Instant
import java.util.*
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userService: UserService,
    private val debtService: DebtService,
    private val eventMemberRepository: EventMemberRepository,
    private val botInstances: List<TelegramBot>
) {

    fun getMyEvents(user: User) = eventMemberRepository.findByUser(user).map { eventRepository.findById(it.eventId) }

    fun createEvent(user: User, request: EventCreateRequest) = eventRepository.save(
        Event(
            name = request.name,
            creator = user,
            lockedDate = null,
            itemGroups = mutableListOf(),
            lendType = Event.LendType.EQUALLY
        )
    ).apply {
        addMember(id, user.telegramId)
    }

    fun isFullyPaid(user: User, id: UUID): Boolean {
        val event = eventRepository.findById(id).getOrElse { throw EventNotFoundException }
        event.validateMembership(user)

        return event.members.size != 1 && event.members.all { debtService.getDebtInfo(it).debts.all { debt -> debt.value.youOwe == BigInteger.ZERO && debt.value.theyOwe == BigInteger.ZERO } }
    }

    fun updateEvent(user: User, id: UUID, request: EventUpdateRequest): Event {
        val event = eventRepository.findById(id).getOrElse { throw EventNotFoundException }
        event.validateOwnership(user)

        return eventRepository.save(event.copy(name = request.name))
    }

    fun getUserDebt(user: User, id: UUID): DebtInfoResponse {
        val event = eventRepository.findById(id).getOrElse { throw EventNotFoundException }
        event.validateMembership(user)

        val member = eventMemberRepository.findByUserAndEventId(user, event.id).getOrNull() ?: throw UserNotFoundException
        return debtService.getDebtInfo(member)
    }

    @Transactional
    fun deleteEvent(user: User, id: UUID): ResponseEntity<StatusResponse> {
        val event = eventRepository.findById(id).getOrElse { throw EventNotFoundException }
        event.validateOwnership(user)

        eventMemberRepository.deleteByEventId(id)
        eventRepository.deleteById(id)
        return respondOk()
    }

    fun getEventById(id: UUID): Event {
        return eventRepository.findById(id).getOrNull() ?: throw EventNotFoundException
    }

    // not user-called, no right verification
    fun addMember(eventId: UUID, userId: Long) {
        val event = eventRepository.findById(eventId).getOrElse { throw EventNotFoundException }
        val user = userService.getUserById(userId) ?: throw UserNotFoundException
        val member = EventMember(userId, event.id, mutableListOf(), mutableListOf(), user)
        eventMemberRepository.save(member)
        event.members += member
        eventRepository.save(event)
        debtService.recalculateDebts(event)
    }

    fun getUserMember(user: User, event: Event): EventMember? {
        return eventMemberRepository.findByUserAndEventId(user, event.id).getOrNull()
    }

    fun lockEvent(user: User, id: UUID): Event {
        val event = eventRepository.findById(id).getOrElse { throw EventNotFoundException }
        event.validateOwnership(user)

        runBlocking {
            botInstances.forEach {
                message { "Пришло время платить по долгам в событии ${event.name}!" }.send(user.telegramId, it)
            }
        }

        return eventRepository.save(event.copy(lockedDate = Instant.now()))
    }

}

fun Event.validateOwnership(user: User) {
//    if (creator.telegramId != user.telegramId) throw ForbiddenException
}

fun Event.validateMembership(user: User) {
//    if (creator.telegramId != user.telegramId) throw ForbiddenException
}

fun Event.validateNotLocked() {
//    if (lockedDate != null) throw ForbiddenException
}
