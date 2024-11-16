package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.service.UserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    @Operation(description = "Получить юзера, под которым авторизован пользователь")
    fun me(@AuthenticationPrincipal user: User) = userService.me(user)

    @GetMapping("/{id}")
    @Operation(description = "Получить другого юзера по айди")
    fun getUser(@PathVariable id: Long) = userService.getUserById(id)

}