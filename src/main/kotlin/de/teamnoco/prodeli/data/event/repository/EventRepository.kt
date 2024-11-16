package de.teamnoco.prodeli.data.event.repository

import de.teamnoco.prodeli.data.event.model.Event
import de.teamnoco.prodeli.data.event.model.EventMember
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
interface EventRepository : JpaRepository<Event, UUID> {

    fun findAllByMembersContains(member: EventMember): List<Event>

}