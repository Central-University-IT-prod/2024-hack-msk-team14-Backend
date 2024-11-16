package de.teamnoco.prodeli.web.exception

import de.teamnoco.prodeli.web.response.StatusCodeException
import de.teamnoco.prodeli.web.response.WebError
import de.teamnoco.prodeli.web.response.WebErrorException
import de.teamnoco.prodeli.web.response.asResponseEntity
import jakarta.validation.ConstraintViolationException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import org.springframework.security.access.AccessDeniedException as SpringAccessDeniedException

@Order(1000)
@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException) =
        de.teamnoco.prodeli.web.response.Errors.InvalidBody(e.constraintViolations).asResponseEntity()

    @ExceptionHandler(StatusCodeException::class)
    fun handleStatusCodeException(e: StatusCodeException) = e.response.asResponseEntity()

    @ExceptionHandler(WebErrorException::class)
    fun handleWebError(e: WebErrorException) = (e::class.findAnnotation<WebError>()?.errorId ?: 0).let {
        val arg = e::class.memberProperties.firstOrNull()?.call(e) ?: Unit
        de.teamnoco.prodeli.web.response.Errors.getById(it)?.invoke(arg)?.asResponseEntity()
            ?: error("Выброшен WebErrorException с неизвестным кодом ошибки: $it.")
    }

    @ExceptionHandler(CredentialsExpiredException::class)
    fun handleCredentialsExpiredException(e: CredentialsExpiredException) =
        de.teamnoco.prodeli.web.response.Errors.InvalidCredentials.asResponseEntity()

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException) =
        de.teamnoco.prodeli.web.response.Errors.InvalidCredentials.asResponseEntity()

    @ExceptionHandler(SpringAccessDeniedException::class)
    fun handleAccessDeniedException(e: SpringAccessDeniedException) =
        de.teamnoco.prodeli.web.response.Errors.AccessDenied.asResponseEntity()

    @Suppress("UNCHECKED_CAST")
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest
    ): ResponseEntity<Any> {
        ex.printStackTrace()
        return de.teamnoco.prodeli.web.response.Errors.HttpMessageNotReadable(ex.message ?: "")
            .asResponseEntity() as ResponseEntity<Any>
    }

    @Suppress("UNCHECKED_CAST")
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest
    ) = de.teamnoco.prodeli.web.response.Errors.MethodArgumentNotValid(ex.message)
        .asResponseEntity() as ResponseEntity<Any>
}