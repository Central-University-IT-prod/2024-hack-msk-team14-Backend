package de.teamnoco.prodeli.data.event.repository

import de.teamnoco.prodeli.data.event.model.EventMember
import de.teamnoco.prodeli.data.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventMemberRepository : JpaRepository<EventMember, EventMember.MemberId> {
    fun findByUserAndEventId(user: User, event: UUID): Optional<EventMember>
    fun findByUser(user: User): List<EventMember>
    fun deleteByEventId(eventId: UUID)
}