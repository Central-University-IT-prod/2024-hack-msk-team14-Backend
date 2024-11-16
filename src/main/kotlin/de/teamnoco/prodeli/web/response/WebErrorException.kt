package de.teamnoco.prodeli.web.response

/**
 * Специальный класс, необходимый для реализации веб ошибок.
 * Должен быть помечен аннотацией [WebError] с идентификатором ошибки.
 * Если ошибка содержит аргумент, необходимо передать его в качестве единственного параметр класса ошибки.
 * Пример:
 * ```
 * @WebError(999)
 * data class SampleError(val reason: String) : WebErrorException()
 *
 * // ...
 *
 * object Errors {
 *     // ...
 *     val SampleError = error<String>(statusCode = 10) { "Ошибка: $it" }
 *     // ...
 * }
 *
 * // ...
 *
 * @RestController("/test")
 * class TestController {
 *     @GetMapping
 *     fun test() = throw SampleError("Some reason")
 * }
 * ```
 *
 * Эта ошибка будет сериализована в [de.teamnoco.prodeli.web.exception.ExceptionHandler] в указанную ошибку.
 */
open class WebErrorException : RuntimeException()

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebError(val errorId: Int)
