package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.data.item.dto.AddEventItemGroupRequest
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.service.ItemService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@RestController
@RequestMapping("/api/event/{eventId}/item")
class ItemController(
    val itemService: ItemService
) {

    @GetMapping("/")
    @Operation(description = "Получить все группы товаров в мероприятии (например, чек из ресторана)")
    fun getEventItemGroups(@AuthenticationPrincipal user: User, @PathVariable eventId: UUID) =
        itemService.getEventItemGroups(user, eventId)

    @PostMapping("/")
    @Operation(description = "Добавить новую группу товаров (например, чек из ресторана)")
    fun addEventItemGroup(
        @AuthenticationPrincipal user: User,
        @PathVariable eventId: UUID,
        @RequestBody request: AddEventItemGroupRequest
    ) =
        itemService.addEventItemGroup(user, eventId, request)

    @GetMapping("/{groupId}")
    @Operation(description = "Получить группу товаров по айди")
    fun getEventItemGroup(
        @AuthenticationPrincipal user: User,
        @PathVariable eventId: UUID,
        @PathVariable groupId: UUID
    ) =
        itemService.getEventItemGroup(user, eventId, groupId)

    @DeleteMapping("/{groupId}")
    @Operation(description = "Удалить группу товаров")
    fun deleteEventItemGroup(
        @AuthenticationPrincipal user: User,
        @PathVariable eventId: UUID,
        @PathVariable groupId: UUID
    ) = itemService.deleteEventItemGroup(user, eventId, groupId)

    @PutMapping("/{groupId}/items")
    @Operation(description = "Обновить товары в группе")
    fun updateEventItems(
        @AuthenticationPrincipal user: User,
        @PathVariable eventId: UUID,
        @PathVariable groupId: UUID,
        @RequestBody request: List<AddEventItemGroupRequest.RequestEventItem>
    ) =
        itemService.updateEventItems(user, eventId, groupId, request)

}