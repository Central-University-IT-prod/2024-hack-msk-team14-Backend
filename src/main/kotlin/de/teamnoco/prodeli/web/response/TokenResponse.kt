package de.teamnoco.prodeli.web.response

data class TokenResponse(
    val isNew: Boolean,
    val token: String
)