package de.teamnoco.prodeli.web.controller

import de.teamnoco.prodeli.web.response.respondOk
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ping")
class PingController {

    @GetMapping
    @Operation(description = "Проверить, что апишка жива ^-^")
    fun ping() = respondOk("Pong!")

}
