package de.teamnoco.prodeli.web.request.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class WebAppAuthRequest(
    val params: String,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    @JsonProperty(required = false)
    val username: String?
)