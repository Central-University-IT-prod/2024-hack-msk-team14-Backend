package de.teamnoco.prodeli.web.response

import jakarta.validation.ConstraintViolation

@Suppress("UNUSED")
object Errors {
    private val ERROR_BY_IDS = mutableMapOf<Int, (Any?) -> StatusResponse.Error>()

    val InvalidBody =
        error<Collection<ConstraintViolation<*>>>(statusCode = 1) {
            "Invalid body was provided to request. Causes: ${it.joinToString(", ") { violation -> violation.message }}"
        }
    val AuthRequired = error(
        "Authentication required for this endpoint.",
        2,
        401
    )
    val InvalidCredentials =
        error("Invalid credentials.", 3, 401)
    val UserAlreadyExists =
        error("User already exists.", 4, 409)
    val CredentialsExpired =
        error("Credentials expired.", 5, 401)
    val HttpMessageNotReadable =
        error<String>(statusCode = 6) { "Http message is not readable: $it" }
    val MethodArgumentNotValid =
        error<String>(statusCode = 7) { "Method argument is not valid: $it" }
    val AccessDenied = error("Access Denied.", 8, 403)
    val HashCheckFailed = error("Hash check failed.", 9, 400)
    val EventNotFound = error("Event not found.", 10, 404)
    val InviteNotFound = error("Invite not found.", 11, 404)
    val Forbidden = error("Forbidden.", 12, 403)
    val UserNotFound = error("User not found.", 13, 404)
    val OperationGroupNotFound = error("Operation group not found.", 14, 404)

    private fun error(message: String, statusCode: Int, httpCode: Int) =
        StatusResponse.Error(message, statusCode, httpCode).apply {
            ERROR_BY_IDS[statusCode] = { this }
        }

    private inline fun <reified T : Any> error(
        httpCode: Int = 400,
        statusCode: Int = 1,
        crossinline messageBuilder: (T) -> String
    ) = ErrorBuilder<T>(httpCode, statusCode, messageBuilder)
        .apply {
            ERROR_BY_IDS[statusCode] =
                { this.invoke(it as T) }
    }

    fun getById(id: Int) = ERROR_BY_IDS[id]
}