package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.service.AuthService
import de.teamnoco.prodeli.web.request.auth.WebAppAuthRequest
import de.teamnoco.prodeli.web.response.TokenResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/sign-in")
    @Operation(description = "Авторизоваться по данным, которые передаёт Telegram")
    fun signIn(@RequestBody request: WebAppAuthRequest) =
        authService.signIn(request).let {
            try {
                TokenResponse(it.isNew, it.currentToken)
            } finally {
                it.isNew = false
            }
        }
}