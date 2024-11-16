package de.teamnoco.prodeli.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.teamnoco.prodeli.web.request.check.CheckRequest
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

@RestController
@RequestMapping("/api/check")
class CheckController(
    @Value("\${proverkachecka.token}")
    val apiToken: String
) {
    @PostMapping
    @Operation(description = "Получить информацию про чек с QR кода")
    fun check(@RequestBody request: CheckRequest, httpServletRequest: HttpServletRequest) =
        HttpClient.newHttpClient().use {
            val objectMapper = ObjectMapper()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://proverkacheka.com/api/v1/check/get"))
                .header("Content-Type", "application/json")
                .POST(
                    BodyPublishers.ofString(
                        objectMapper.writeValueAsString(
                            mapOf(
                                "token" to apiToken,
                                "qrraw" to request.qrraw
                            )
                        )
                    )
                )
                .build()

            objectMapper.readValue<Map<String, Any>>(it.send(request, HttpResponse.BodyHandlers.ofString()).body())
        }
}