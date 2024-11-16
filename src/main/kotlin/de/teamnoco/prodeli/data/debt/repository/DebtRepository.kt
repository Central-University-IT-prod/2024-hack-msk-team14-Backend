package de.teamnoco.prodeli.data.debt.repository

import de.teamnoco.prodeli.data.debt.model.Debt
import de.teamnoco.prodeli.data.event.model.EventMember
import de.teamnoco.prodeli.data.item.model.EventItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DebtRepository : JpaRepository<Debt, UUID> {
    fun findByItem(item: EventItem): List<Debt>
    fun findAllByMember(member: EventMember): List<Debt>
}