package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.service.InviteService
import de.teamnoco.prodeli.web.request.invite.NewInviteRequest
import de.teamnoco.prodeli.web.response.InviteResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/invite")
class InviteController(private val inviteService: InviteService) {
    @PostMapping("/new")
    @Operation(description = "Создать новый инвайт айди (используется в ссылке для участия в мероприятии: https://t.me/prodelibot?start=invite<id>)")
    fun newInvite(@AuthenticationPrincipal user: User, @RequestBody request: NewInviteRequest) =
        inviteService.createInvite(UUID.fromString(request.eventId)).apply { InviteResponse(this) }
}