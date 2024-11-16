package de.teamnoco.prodeli.web.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import de.teamnoco.prodeli.util.model.SerializableModel
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity

sealed class StatusResponse(val message: String, val statusCode: Int, @JsonIgnore val httpCode: Int) {
    class Ok(message: String = "ok") : StatusResponse(message, 0, 200) {
        override fun toString(): String {
            return "StatusResponse.Ok(message=$message)"
        }
    }

    class Error(message: String, statusCode: Int, httpCode: Int = 400) : StatusResponse(message, statusCode, httpCode) {
        override fun toString(): String {
            return "StatusResponse.Error(message='$message', statusCode=$statusCode, httpCode=$httpCode)"
        }
    }

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(message: String, statusCode: Int) =
            if (statusCode == 0) Ok(message)
            else Error(message, statusCode)
    }
}

data class StatusCodeException(val response: StatusResponse) : RuntimeException(response.message)

inline fun <reified T : Any> ErrorBuilder(
    httpCode: Int = 400, statusCode: Int = 1, crossinline messageBuilder: (T) -> String
): (T) -> StatusResponse.Error = { StatusResponse.Error(messageBuilder(it), statusCode, httpCode) }

fun SerializableModel.asResponseEntity(httpStatus: Int = 200): ResponseEntity<Map<String, Any>> =
    ResponseEntity.status(httpStatus).body(serialize())

fun StatusResponse.asResponseEntity(): ResponseEntity<StatusResponse> = ResponseEntity.status(httpCode).body(this)

fun respondOk(message: String) = StatusResponse.Ok(message).asResponseEntity()

fun respondOk() = StatusResponse.Ok().asResponseEntity()

fun Any.respondOk() = de.teamnoco.prodeli.web.response.respondOk()

fun HttpServletResponse.sendResponse(response: StatusResponse) {
    status = response.httpCode
    contentType = "application/json"
    writer.write(ObjectMapper().writeValueAsString(response))
}
