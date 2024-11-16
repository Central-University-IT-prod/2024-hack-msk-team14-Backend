package de.teamnoco.prodeli.service

import de.teamnoco.prodeli.data.event.exception.EventNotFoundException
import de.teamnoco.prodeli.data.event.repository.EventRepository
import de.teamnoco.prodeli.data.invite.exception.InviteNotFoundException
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.util.random
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*

@Service
class InviteService(
    private val eventService: EventService,
    val eventRepository: EventRepository
) {
    private val invites = mutableMapOf<String, UUID>()

    fun createInvite(eventId: UUID): String =
        generateInviteId().apply {
            if (eventRepository.existsById(eventId)) {
                invites[this] = eventId
            } else {
                throw EventNotFoundException
            }
        }

    fun generateInviteId() = buildString {
        repeat(16) {
            TOKEN_SYMBOLS.random(RANDOM)
        }
    }

    fun applyInvite(user: User, id: String) {
        val eventId = invites[id] ?: throw InviteNotFoundException
        eventService.addMember(eventId, user.telegramId)
    }

    companion object {
        private val RANDOM = SecureRandom.getInstanceStrong()
        private val TOKEN_SYMBOLS = ('0'..'9') + ('a'..'z') + ('A'..'Z')
    }
}