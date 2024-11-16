package de.teamnoco.prodeli.service

import de.teamnoco.prodeli.data.event.model.Event
import de.teamnoco.prodeli.data.event.model.toEventMember
import de.teamnoco.prodeli.data.event.repository.EventRepository
import de.teamnoco.prodeli.data.exception.ForbiddenException
import de.teamnoco.prodeli.data.item.dto.AddEventItemGroupRequest
import de.teamnoco.prodeli.data.item.model.EventItem
import de.teamnoco.prodeli.data.item.model.ItemGroup
import de.teamnoco.prodeli.data.item.repository.ItemGroupRepository
import de.teamnoco.prodeli.data.user.model.User
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class ItemService(
    private val eventService: EventService,
    private val itemGroupRepository: ItemGroupRepository,
    private val eventRepository: EventRepository,
    private val debtService: DebtService
) {

    fun getEventItemGroups(user: User, eventId: UUID): List<ItemGroup> {
        val event = eventService.getEventById(eventId)
        eventService.getUserMember(user, event) ?: throw ForbiddenException

        event.validateMembership(user)
        event.validateNotLocked()

        return event.itemGroups
    }

    fun addEventItemGroup(user: User, eventId: UUID, request: AddEventItemGroupRequest): ItemGroup {
        val event = eventService.getEventById(eventId).also { it.validateOwnership(user) }
        event.validateOwnership(user)

        val group = itemGroupRepository.save(ItemGroup(
            name = request.name,
            items = mutableListOf(),
            lender = user.toEventMember(event)
        )).apply {
            items.addAll(request.items.map {
                it.asEventItem(event, id, user)
            })
        }

        itemGroupRepository.save(group)
        event.itemGroups.add(group)
        eventRepository.save(event)

        debtService.recalculateDebts(event)

        return group
    }

    fun getEventItemGroup(user: User, eventId: UUID, groupId: UUID): ItemGroup {
        val event = eventService.getEventById(eventId).also { it.validateOwnership(user) }
        event.validateOwnership(user)
        val group = itemGroupRepository.findById(groupId).getOrElse { error("") } // todo
        if (group !in event.itemGroups) error("") // todo
        return group
    }

    fun deleteEventItemGroup(user: User, eventId: UUID, groupId: UUID) {
        val event = eventService.getEventById(eventId).also { it.validateOwnership(user) }
        event.validateOwnership(user)
        val group = itemGroupRepository.findById(groupId).getOrElse { error("") } // todo
        if (group !in event.itemGroups) error("") // todo
        itemGroupRepository.delete(group)
    }

    fun updateEventItems(
        user: User,
        eventId: UUID,
        groupId: UUID,
        items: List<AddEventItemGroupRequest.RequestEventItem>
    ): ItemGroup {
        val event = eventService.getEventById(eventId).also { it.validateOwnership(user) }
        event.validateOwnership(user)
        val group = itemGroupRepository.findById(groupId).getOrElse { error("") } // todo
        if (group !in event.itemGroups) error("") // todo
        group.items.clear()
        group.items.addAll(items.map { it.asEventItem(event, groupId, user) })
        itemGroupRepository.save(group)
        debtService.recalculateDebts(event)
        return group
    }

    fun AddEventItemGroupRequest.RequestEventItem.asEventItem(event: Event, groupId: UUID, lender: User): EventItem =
        EventItem(
            groupId = groupId,
            name = name,
            price = price,
            quantity = quantity,
        )
}