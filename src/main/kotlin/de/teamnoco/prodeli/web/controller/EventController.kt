package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.data.event.dto.EventCreateRequest
import de.teamnoco.prodeli.data.event.dto.EventUpdateRequest
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.service.EventService
import de.teamnoco.prodeli.service.validateOwnership
import de.teamnoco.prodeli.web.response.respondOk
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@RestController
@RequestMapping("/api/event")
class EventController(
    val eventService: EventService
) {

    @GetMapping("/my")
    @Operation(description = "Получить все мероприятия, в которых участвует пользователь")
    fun getMyEvents(@AuthenticationPrincipal user: User) = eventService.getMyEvents(user)

    @GetMapping("/{id}")
    @Operation(description = "Получить мероприятие по айдишнику")
    fun getEventById(@AuthenticationPrincipal user: User, @PathVariable id: UUID) =
        eventService.getEventById(id).also { it.validateOwnership(user) }

    @GetMapping("/{id}/isFullyPaid")
    @Operation(description = "Узнать, оплачено ли полностью мероприятие")
    fun isFullyPaid(@AuthenticationPrincipal user: User, @PathVariable id: UUID) =
        eventService.isFullyPaid(user, id)

    @GetMapping("/{id}/myDebt")
    @Operation(description = "Получить информацию про долги пользователя другим и про долги других пользователей ему (внутри мероприятия). Не вызывать до финализации.")
    fun getMyDebt(@AuthenticationPrincipal user: User, @PathVariable id: UUID) =
        eventService.getUserDebt(user, id)

    @PostMapping("/")
    @Operation(description = "Создать новое мероприятие")
    fun createEvent(@AuthenticationPrincipal user: User, @RequestBody request: EventCreateRequest) =
        eventService.createEvent(user, request)

    @PutMapping("/{id}")
    @Operation(description = "Обновить мероприятие (пока что только название)")
    fun updateEvent(
        @AuthenticationPrincipal user: User,
        @PathVariable id: UUID,
        @RequestBody request: EventUpdateRequest
    ) = eventService.updateEvent(user, id, request)

    @PostMapping("/{id}/lock")
    @Operation(description = "Финализировать мероприятие, т.е. запретить добавлять новые чеки, редактировать их и так далее. НЕЛЬЗЯ ОТМЕНИТЬ!")
    fun lockEvent(@AuthenticationPrincipal user: User, @PathVariable id: UUID) =
        eventService.lockEvent(user, id).respondOk()

    @DeleteMapping("/{id}")
    @Operation(description = "Удалить мероприятие")
    fun deleteEvent(@AuthenticationPrincipal user: User, @PathVariable id: UUID) =
        eventService.deleteEvent(user, id).respondOk()

}