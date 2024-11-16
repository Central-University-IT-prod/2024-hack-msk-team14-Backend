package de.teamnoco.prodeli.service

import de.teamnoco.prodeli.data.debt.dto.DebtInfoResponse
import de.teamnoco.prodeli.data.debt.dto.DebtUpdateRequest
import de.teamnoco.prodeli.data.debt.exception.DebtNotFoundException
import de.teamnoco.prodeli.data.debt.model.Debt
import de.teamnoco.prodeli.data.debt.repository.DebtRepository
import de.teamnoco.prodeli.data.event.exception.EventNotFoundException
import de.teamnoco.prodeli.data.event.model.Event
import de.teamnoco.prodeli.data.event.model.EventMember
import de.teamnoco.prodeli.data.event.repository.EventMemberRepository
import de.teamnoco.prodeli.data.event.repository.EventRepository
import de.teamnoco.prodeli.data.item.model.EventItem
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.data.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class DebtService(
    private val userRepository: UserRepository,
    private val debtRepository: DebtRepository,
    private val eventMemberRepository: EventMemberRepository,
    private val eventRepository: EventRepository
) {
    fun recalculateDebts(event: Event) {
        val eventItems = event.itemGroups.flatMap { it.items }
        val lendType = event.lendType
        val members = event.members
        when (lendType) {
            Event.LendType.EQUALLY -> {
                clearDebts(event)

                eventItems.forEach { eventItem ->
                    val perMemberPrice = eventItem.price / members.size.toBigInteger()
                    eventMemberRepository.saveAll(members.onEach { member ->
                        val debt = Debt(sum = perMemberPrice, item = eventItem, member = member, paid = false)
                        if (!debtRepository.existsById(debt.id)) debtRepository.save(debt)
                        member.debts.add(debt)
                    })
                }
            }

            Event.LendType.MEMBERS_SELECT, Event.LendType.OWNER_SELECT -> {
                members.forEach {
                    if (it.debts.isEmpty() && it.lendItemGroups.isEmpty()) {
                        eventItems.forEach { eventItem ->
                            addItem(it, eventItem)
                        }
                    }
                }
            }
        }
    }

    fun addItem(member: EventMember, item: EventItem) {
        val debts = getItemDebts(item)
        val newPrice = item.price / (debts.size + 1).toBigInteger()
        debtRepository.saveAll(debts.map {
            it.copy(sum = newPrice) // перерасчитываем долг других
        } + Debt(sum = newPrice, item = item, member = member, paid = false))
    }

    fun removeItem(member: EventMember, item: EventItem) {
        val debts = getItemDebts(item)
        val newPrice = item.price / (debts.size - 1).toBigInteger()
        debtRepository.saveAll(debts.map {
            it.copy(sum = newPrice) // перерасчитываем долг других
        })
    }

    fun getMemberDebt(member: EventMember, item: EventItem) =
        getItemDebts(item).find { it.member.userId == member.userId }

    fun clearDebts(event: Event) {
        val members = event.members
        members.forEach { clearDebts(it) }
    }

    fun clearDebts(member: EventMember) {
        member.debts.forEach {
            debtRepository.delete(it)
        }

        member.debts.clear()
    }

    fun getItemDebts(item: EventItem) =
        debtRepository.findByItem(item)

    fun getTotalUnpaidDebt(debtor: EventMember, lender: EventMember): BigInteger {
        if (debtor.eventId != lender.eventId) error("")

        val debts = debtRepository.findAllByMember(debtor)

        return debts.filter { it.item == lender.user && !it.paid }.sumOf { it.sum }
    }

    fun getDebtInfo(debtor: EventMember): DebtInfoResponse {
        val event = eventRepository.findById(debtor.eventId).getOrNull() ?: throw EventNotFoundException
        val eventMembers = event.members.filter { it.userId != debtor.userId }

        return DebtInfoResponse(eventMembers.associate { lender ->
            var youOwe = getTotalUnpaidDebt(debtor, lender)
            var theyOwe = getTotalUnpaidDebt(lender, debtor)

            youOwe = (theyOwe - youOwe).max(BigInteger.ZERO)
            theyOwe = (youOwe - theyOwe).max(BigInteger.ZERO)

            lender.userId to DebtInfoResponse.DebtInfo(
                youOwe, theyOwe
            )
        })
    }

    fun updateDebt(user: User, debtId: UUID, request: DebtUpdateRequest): Debt {
        val debt = debtRepository.findById(debtId).getOrNull() ?: throw DebtNotFoundException
        debt.validateOwnership(user)

        return debtRepository.save(debt.copy(paid = request.paid))
    }

}

fun Debt.validateOwnership(user: User) {
//    if (member.userId != user.telegramId) throw ForbiddenException
}